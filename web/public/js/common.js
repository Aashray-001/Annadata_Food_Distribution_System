/* Annadata web edition - shared helpers and views used by every dashboard */

const $ = (sel, root) => (root || document).querySelector(sel);

function esc(v) {
  return String(v == null ? '' : v).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

async function api(path, method, body) {
  const opts = { method: method || 'GET', headers: {}, credentials: 'same-origin' };
  if (body !== undefined) {
    opts.headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(body);
  }
  const res = await fetch('/api/' + path, opts);
  let data = null;
  try { data = await res.json(); } catch (e) { /* empty body */ }
  if (res.status === 401 && !location.pathname.endsWith('index.html') && location.pathname !== '/') {
    location.href = 'index.html';
    throw new Error('Please log in');
  }
  if (!res.ok) throw new Error((data && data.error) || 'Request failed (' + res.status + ')');
  return data;
}

function toast(msg, isError) {
  let t = $('#toast');
  if (!t) { t = document.createElement('div'); t.id = 'toast'; document.body.appendChild(t); }
  t.textContent = msg;
  t.className = isError ? 'error' : '';
  t.style.display = 'block';
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => { t.style.display = 'none'; }, 3500);
}

/** Run an async UI action and show any error as a toast. */
async function guard(fn) {
  try { return await fn(); } catch (e) { toast(e.message, true); }
}

function formJson(form) {
  const o = {};
  new FormData(form).forEach((v, k) => { o[k] = typeof v === 'string' ? v.trim() : v; });
  return o;
}

const fmtDate = s => (s ? String(s).replace('T', ' ').substring(0, 16) : '—');
const nice = s => (s ? String(s).replace(/_/g, ' ') : '');
const badge = (text, cls) => `<span class="badge ${cls || 'status-' + esc(text)}">${esc(nice(text))}</span>`;

function statCard(label, value, color) {
  return `<div class="stat"><div class="label">${label}</div><div class="value" style="color:${color}">${esc(value)}</div></div>`;
}

function table(headers, rows) {
  if (!rows.length) return '<p class="muted">Nothing to show yet.</p>';
  return `<div class="table-wrap"><table><thead><tr>${headers.map(h => `<th>${h}</th>`).join('')}</tr></thead><tbody>${
    rows.map(r => `<tr>${r.map(c => `<td>${c}</td>`).join('')}</tr>`).join('')}</tbody></table></div>`;
}

/* ---------- app shell: login check, sidebar, view routing ---------- */
async function startApp(role, views, defaultView) {
  let me;
  try { me = await api('auth/me'); } catch (e) { return; }
  if (me.user.role.toLowerCase() !== role) { location.href = me.user.role.toLowerCase() + '.html'; return; }
  window.ME = me.user;
  $('#who').textContent = me.user.name + ' (' + role + ')';
  setBadge(me.unread);

  $('#logout').onclick = async () => { await api('auth/logout', 'POST'); location.href = 'index.html'; };

  // hash format: #view or #view:argument (e.g. #donation:5)
  async function show(hash) {
    let parts = hash.split(':');
    if (!views[parts[0]]) parts = [defaultView];
    document.querySelectorAll('#nav a').forEach(a => a.classList.toggle('active', a.dataset.view === parts[0]));
    const el = $('#content');
    el.onclick = null; el.onchange = null; // views attach their own handlers
    el.innerHTML = '<p class="muted">Loading…</p>';
    await guard(() => views[parts[0]](el, parts[1]));
  }
  window.showView = name => { if (location.hash.slice(1) === name) show(name); else location.hash = name; };
  window.addEventListener('hashchange', () => show(location.hash.slice(1) || defaultView));
  show(location.hash.slice(1) || defaultView);
}

function setBadge(n) {
  const b = $('#notifBadge');
  if (!b) return;
  b.textContent = n;
  b.classList.toggle('hidden', !n);
}
async function refreshBadge() {
  const me = await api('auth/me');
  setBadge(me.unread);
}

/* ---------- donation card (donor + recipient) ---------- */
function donationCard(d, actionsHtml) {
  return `<div class="card">
    <div class="card-row"><h3>${esc(d.title)}</h3>${badge(d.status)}</div>
    <div class="muted">Category: ${esc(nice(d.category))} · Quantity: ${esc(d.quantity)} ${esc(d.unit)} · Source: ${esc(d.sourceName || 'Anonymous')} (${esc(d.sourceType || 'individual')})</div>
    <div>📍 Pickup: ${esc(d.pickupAddress || 'Contact donor')}</div>
    <div class="muted">Expires: ${fmtDate(d.expiryDate)} ${d.expiringSoon ? '<span class="warn">⚠ expiring within 24 hours</span>' : ''}</div>
    ${d.description ? `<p>${esc(d.description)}</p>` : ''}
    <div class="actions">${actionsHtml || ''}</div>
  </div>`;
}

/* ================= shared views ================= */

/* Emergency aid: recipients post, donors/admins respond */
async function viewEmergency(el) {
  const role = ME.role.toLowerCase();
  const list = await api('emergencies');
  const mine = role === 'recipient' ? await api('emergencies/mine') : [];

  const types = ['disaster_relief', 'medical_emergency', 'community_crisis', 'natural_calamity', 'other'];
  const form = role !== 'recipient' ? '' : `
    <div class="actions" style="margin-bottom:12px"><button class="btn" data-act="toggle-form">🚨 Post Emergency Request</button></div>
    <form id="aidForm" class="card hidden">
      <h3>New emergency request</h3>
      <div class="form-grid">
        <div class="full"><label>Title *</label><input name="title" required></div>
        <div><label>Type *</label><select name="emergencyType" required>${types.map(t => `<option value="${t}">${nice(t)}</option>`).join('')}</select></div>
        <div><label>Urgency *</label><select name="urgency"><option value="critical">🔴 Critical</option><option value="high">🟠 High</option><option value="medium" selected>🟡 Medium</option></select></div>
        <div><label>People affected</label><input type="number" name="peopleAffected" min="1" value="10"></div>
        <div><label>Required quantity</label><input type="number" name="requiredQuantity" min="0"></div>
        <div><label>Contact phone</label><input name="contactPhone"></div>
        <div class="full"><label>Location</label><input name="location"></div>
        <div class="full"><label>Required items</label><input name="requiredItems" placeholder="e.g. rice, drinking water, blankets"></div>
        <div class="full"><label>Description</label><textarea name="description"></textarea></div>
      </div>
      <div class="actions"><button class="btn" type="submit">Submit request</button><button class="btn secondary" type="button" data-act="toggle-form">Cancel</button></div>
    </form>`;

  const card = a => `<div class="card emergency ${esc(a.urgency)}">
      <div class="card-row"><h3>${esc(a.title)}</h3><div>${badge(a.urgency, 'urgency-' + esc(a.urgency))} ${badge(a.status)}</div></div>
      <div class="muted">${esc(nice(a.emergencyType))} · ${esc(a.peopleAffected)} people affected · requested by ${esc(a.recipientName || '—')}</div>
      <div>📍 ${esc(a.location || '—')}</div>
      <div>Needed: <b>${esc(a.requiredQuantity)}</b> × ${esc(a.requiredItems || '—')}${a.contactPhone ? ' · ☎ ' + esc(a.contactPhone) : ''}</div>
      ${a.description ? `<p>${esc(a.description)}</p>` : ''}
      <div class="actions">
        ${role === 'donor' || role === 'admin' ? `<button class="btn small" data-act="respond" data-id="${a.id}">🤝 Respond / Pledge help</button>` : ''}
        ${(role === 'recipient' && a.recipientId === ME.id) || role === 'admin' ? `
          <button class="btn small secondary" data-act="status" data-id="${a.id}" data-status="fulfilled">Mark fulfilled</button>
          <button class="btn small danger" data-act="status" data-id="${a.id}" data-status="closed">Close</button>` : ''}
      </div></div>`;

  el.innerHTML = `<h1>🚨 Emergency Aid</h1>${form}
    <h2>Active requests</h2>${list.length ? list.map(card).join('') : '<p class="muted">No active emergencies right now.</p>'}
    ${role === 'recipient' ? `<h2>My requests</h2>${table(['Title', 'Urgency', 'Status', 'Posted'],
      mine.map(a => [esc(a.title), badge(a.urgency, 'urgency-' + esc(a.urgency)), badge(a.status), fmtDate(a.createdAt)]))}` : ''}`;

  el.onclick = e => guard(async () => {
    const t = e.target.closest('[data-act]');
    if (!t) return;
    if (t.dataset.act === 'toggle-form') $('#aidForm').classList.toggle('hidden');
    if (t.dataset.act === 'respond') {
      const msg = prompt('Add an optional note for the requester (what can you provide?)');
      if (msg === null) return;
      await api('emergencies/' + t.dataset.id + '/respond', 'POST', { message: msg });
      toast('Your pledge was sent to the requester.');
      viewEmergency(el);
    }
    if (t.dataset.act === 'status') {
      await api('emergencies/' + t.dataset.id + '/status', 'POST', { status: t.dataset.status });
      toast('Status updated.');
      viewEmergency(el);
    }
  });
  const f = $('#aidForm');
  if (f) f.onsubmit = ev => { ev.preventDefault(); guard(async () => {
    await api('emergencies', 'POST', formJson(f));
    toast('Emergency request posted successfully.');
    viewEmergency(el);
  }); };
}

/* Notifications */
async function viewNotifications(el) {
  const list = await api('notifications');
  el.innerHTML = `<div class="card-row"><h1>🔔 Notifications</h1><button class="btn secondary" data-act="all">Mark all as read</button></div>
    <div class="table-wrap">${list.length ? list.map(n => `
      <div class="notif ${n.read ? '' : 'unread'}" data-act="read" data-id="${n.id}" data-read="${n.read}">
        ${esc(n.message)}<div class="muted">${fmtDate(n.createdAt)}</div></div>`).join('') : '<p style="padding:14px" class="muted">You have no notifications.</p>'}</div>`;
  el.onclick = e => guard(async () => {
    const t = e.target.closest('[data-act]');
    if (!t) return;
    if (t.dataset.act === 'all') { await api('notifications/read-all', 'POST'); await viewNotifications(el); }
    if (t.dataset.act === 'read' && t.dataset.read === 'false') {
      await api('notifications/' + t.dataset.id + '/read', 'POST');
      t.classList.remove('unread'); t.dataset.read = 'true';
    }
    refreshBadge();
  });
}

/* Donation source analytics (admin) */
const SOURCE_COLORS = { individual: '#1565c0', organization: '#2e7d32', government: '#e65100' };

async function viewAnalytics(el, embedded) {
  const a = await api('analytics');
  const bars = a.total === 0 ? '<p class="muted">No donation data yet.</p>' : a.bySource.map(s => `
    <div class="bar-row"><div class="name">${esc(s.source)}</div>
      <div class="bar-track"><div class="bar-fill" style="width:${s.percent}%;background:${SOURCE_COLORS[s.source] || '#6d4c41'}"></div></div>
      <div>${s.count} (${s.percent}%)</div></div>`).join('');

  // group the monthly query into one row per month
  const months = {};
  a.monthly.forEach(r => {
    const key = r.year + '-' + String(r.month).padStart(2, '0');
    (months[key] = months[key] || {})[r.source_type] = r.count;
  });
  const monthRows = Object.keys(months).sort().map(k => [k,
    months[k].individual || 0, months[k].organization || 0, months[k].government || 0]);

  el.innerHTML = `${embedded ? '' : '<h1>📊 Donation Source Analytics</h1>'}
    <div class="card"><h3>Donations by source type</h3>${bars}</div>
    <div class="stats">${statCard('📦 Total donations', a.total, '#1b5e20')}${a.bySource.map(s =>
      statCard(esc(s.source) + ' (' + s.percent + '%)', s.count, SOURCE_COLORS[s.source] || '#6d4c41')).join('')}</div>
    <h3>Monthly trend</h3>${table(['Month', 'Individual', 'Organization', 'Government'], monthRows)}`;
}

/* Weekly distribution schedule */
const DAYS = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];
const SLOTS = ['morning', 'afternoon', 'evening'];

async function viewSchedule(el) {
  const admin = ME.role.toLowerCase() === 'admin';
  const [today, all, places] = await Promise.all([api('schedule/today'), api('schedule'), api('places')]);
  const statuses = ['scheduled', 'assigned', 'in_transit', 'delivered', 'completed'];

  const todayCard = s => `<div class="card"><div class="card-row"><h3>${esc(s.placeName)}</h3>${badge(s.status)}</div>
    <div>${esc(nice(s.timeSlot))} · ${esc(s.foodType)} · ${esc(s.quantityTarget)} ${esc(s.unit || '')}</div>
    <div class="muted">Volunteer: ${esc(s.assignedVolunteer || 'unassigned')} ${esc(s.volunteerPhone || '')}</div>
    ${admin ? `<div class="actions"><select data-act="set-status" data-id="${s.id}" style="width:auto">${statuses.map(x =>
      `<option value="${x}" ${x === s.status ? 'selected' : ''}>${nice(x)}</option>`).join('')}</select></div>` : ''}</div>`;

  const grid = SLOTS.map(slot => `<tr><td>${nice(slot)}</td>${DAYS.map(day => `<td>${
    all.filter(s => s.dayOfWeek === day && s.timeSlot === slot).map(s => `<div class="slot-card">
      <div class="place">${esc(s.placeName)}</div><div>${esc(s.foodType)}</div>
      <div>${badge(s.status)}</div>${admin ? `<a href="#" data-act="del" data-id="${s.id}" class="warn">remove</a>` : ''}</div>`).join('')}</td>`).join('')}</tr>`).join('');

  const form = !admin ? '' : `
    <div class="actions" style="margin:12px 0"><button class="btn" data-act="toggle-form">＋ Add schedule entry</button></div>
    <form id="schedForm" class="card hidden"><div class="form-grid">
      <div><label>Place *</label><select name="placeId" required>${places.filter(p => p.active).map(p => `<option value="${p.id}">${esc(p.name)}</option>`).join('')}</select></div>
      <div><label>Day *</label><select name="dayOfWeek">${DAYS.map(d => `<option>${d}</option>`).join('')}</select></div>
      <div><label>Time slot *</label><select name="timeSlot">${SLOTS.map(d => `<option>${d}</option>`).join('')}</select></div>
      <div><label>Food type *</label><input name="foodType" required></div>
      <div><label>Target quantity</label><input type="number" name="quantityTarget" min="0"></div>
      <div><label>Unit</label><input name="unit" placeholder="kg, plates…"></div>
      <div><label>Volunteer</label><input name="assignedVolunteer"></div>
      <div><label>Volunteer phone</label><input name="volunteerPhone"></div>
    </div><div class="actions"><button class="btn" type="submit">Save entry</button></div></form>`;

  el.innerHTML = `<h1>📅 Distribution Schedule</h1>
    <h2>Today's distribution</h2>${today.length ? today.map(todayCard).join('') : '<p class="muted">Nothing scheduled for today.</p>'}
    <h2>Weekly grid</h2>${form}
    <div class="table-wrap"><table class="week"><thead><tr><th></th>${DAYS.map(d => `<th>${d}</th>`).join('')}</tr></thead><tbody>${grid}</tbody></table></div>`;

  el.onclick = e => guard(async () => {
    const t = e.target.closest('[data-act]');
    if (!t) return;
    if (t.dataset.act === 'toggle-form') $('#schedForm').classList.toggle('hidden');
    if (t.dataset.act === 'del') {
      e.preventDefault();
      if (!confirm('Remove this schedule entry?')) return;
      await api('schedule/' + t.dataset.id, 'DELETE');
      viewSchedule(el);
    }
  });
  el.onchange = e => guard(async () => {
    const t = e.target.closest('[data-act="set-status"]');
    if (!t) return;
    await api('schedule/' + t.dataset.id + '/status', 'POST', { status: t.value });
    toast('Status updated.');
    viewSchedule(el);
  });
  const f = $('#schedForm');
  if (f) f.onsubmit = ev => { ev.preventDefault(); guard(async () => {
    await api('schedule', 'POST', formJson(f));
    toast('Schedule added.');
    viewSchedule(el);
  }); };
}

/* Distribution places */
const PLACE_CATEGORIES = ['dog_food_service', 'old_age_home', 'temple', 'orphanage', 'community_kitchen', 'hospital', 'school', 'other'];
const PLACE_ICONS = { dog_food_service: '🐕', old_age_home: '🏠', temple: '🛕', orphanage: '👶', community_kitchen: '🍲', hospital: '🏥', school: '🏫' };

async function viewPlaces(el, filter) {
  const admin = ME.role.toLowerCase() === 'admin';
  let places = await api('places');
  filter = filter || 'All';
  if (filter !== 'All') places = places.filter(p => p.category === filter);

  el.innerHTML = `<h1>📍 Distribution Places</h1>
    <div class="toolbar"><select id="catFilter"><option>All</option>${PLACE_CATEGORIES.map(c =>
      `<option value="${c}" ${c === filter ? 'selected' : ''}>${nice(c)}</option>`).join('')}</select>
      ${admin ? '<button class="btn" data-act="toggle-form">＋ Add place</button>' : ''}</div>
    ${admin ? `<form id="placeForm" class="card hidden"><div class="form-grid">
      <div><label>Name *</label><input name="name" required></div>
      <div><label>Category *</label><select name="category">${PLACE_CATEGORIES.map(c => `<option value="${c}">${nice(c)}</option>`).join('')}</select></div>
      <div class="full"><label>Address</label><input name="address"></div>
      <div><label>Contact person</label><input name="contactPerson"></div>
      <div><label>Contact phone</label><input name="contactPhone"></div>
      <div><label>Operating hours</label><input name="operatingHours"></div>
      <div><label>Capacity</label><input type="number" name="capacity" min="0"></div>
    </div><div class="actions"><button class="btn" type="submit">Save place</button></div></form>` : ''}
    ${table(['Place', 'Category', 'Address', 'Contact', 'Hours', 'Capacity', 'Status'].concat(admin ? [''] : []),
      places.map(p => [`<b>${esc(p.name)}</b>`, (PLACE_ICONS[p.category] || '📌') + ' ' + esc(nice(p.category)), esc(p.address),
        esc(p.contactPerson) + '<br><span class="muted">' + esc(p.contactPhone) + '</span>', esc(p.operatingHours), esc(p.capacity),
        p.active ? badge('active', 'status-available') : badge('inactive', 'status-closed')]
        .concat(admin ? [`<button class="btn small secondary" data-act="active" data-id="${p.id}" data-active="${!p.active}">${p.active ? 'Deactivate' : 'Activate'}</button>`] : []))
    )}`;

  $('#catFilter').onchange = e => viewPlaces(el, e.target.value);
  el.onclick = e => guard(async () => {
    const t = e.target.closest('[data-act]');
    if (!t) return;
    if (t.dataset.act === 'toggle-form') $('#placeForm').classList.toggle('hidden');
    if (t.dataset.act === 'active') {
      await api('places/' + t.dataset.id + '/active', 'POST', { active: t.dataset.active === 'true' });
      viewPlaces(el, filter);
    }
  });
  const f = $('#placeForm');
  if (f) f.onsubmit = ev => { ev.preventDefault(); guard(async () => {
    await api('places', 'POST', formJson(f));
    toast('Place added successfully.');
    viewPlaces(el, filter);
  }); };
}
