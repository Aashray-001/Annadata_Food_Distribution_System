/* Donor dashboard */
const CATS = ['cooked', 'raw', 'packaged', 'beverages', 'dog_food', 'other'];

const donorViews = {
  async dashboard(el) {
    const mine = await api('donations/mine');
    const active = mine.filter(d => ['available', 'claimed', 'accepted'].includes(d.status)).length;
    const done = mine.filter(d => d.status === 'completed').length;
    el.innerHTML = `<h1>Welcome, ${esc(ME.name)}</h1>
      <div class="stats">${statCard('Total donations', mine.length, '#1b5e20')}${statCard('Active', active, '#1565c0')}${statCard('Completed', done, '#00695c')}</div>
      <h2>Recent donations</h2>
      ${mine.length ? mine.slice(0, 3).map(d => donationCard(d, `<a class="btn small secondary" href="#donation:${d.id}">View details</a>`)).join('') : '<p class="muted">You have not posted any donations yet.</p>'}`;
  },

  async create(el) {
    const tomorrow = new Date(Date.now() + 86400000).toISOString().slice(0, 10);
    el.innerHTML = `<h1>➕ Post a Food Donation</h1>
      <form id="donForm" class="card"><div class="form-grid">
        <div class="full"><label>Title *</label><input name="title" required></div>
        <div><label>Category *</label><select name="category">${CATS.map(c => `<option value="${c}">${nice(c)}</option>`).join('')}</select></div>
        <div><label>Quantity *</label><input type="number" name="quantity" min="1" required></div>
        <div><label>Unit *</label><input name="unit" placeholder="kg, plates, packets…" required></div>
        <div><label>Expiry date *</label><input type="date" name="expiryDate" min="${new Date().toISOString().slice(0, 10)}" value="${tomorrow}" required></div>
        <div><label>Source type *</label><select name="sourceType"><option value="individual">Individual</option><option value="organization">Organization</option><option value="government">Government body</option></select></div>
        <div><label>Source name</label><input name="sourceName" value="${esc(ME.name)}"></div>
        <div class="full"><label>Pickup address</label><textarea name="pickupAddress">${esc(ME.address || '')}</textarea></div>
        <div class="full"><label>Description</label><textarea name="description"></textarea></div>
      </div><div class="actions"><button class="btn" type="submit">Post donation</button><button class="btn secondary" type="reset">Clear</button></div></form>`;
    const f = $('#donForm');
    f.onsubmit = ev => { ev.preventDefault(); guard(async () => {
      await api('donations', 'POST', formJson(f));
      toast('Donation created successfully!');
      showView('donations');
    }); };
  },

  async donations(el) {
    const mine = await api('donations/mine');
    el.innerHTML = `<h1>📦 My Donations</h1>` + (mine.length
      ? mine.map(d => donationCard(d, `<a class="btn small secondary" href="#donation:${d.id}">View details</a>`)).join('')
      : '<p class="muted">You have not posted any donations yet.</p>');
  },

  async donation(el, id) {
    const d = await api('donations/' + id);
    const reqs = d.requests || [];
    const open = ['available', 'claimed', 'accepted', 'picked_up'].includes(d.status);
    el.innerHTML = `<p><a href="#donations">← Back to my donations</a></p>
      ${donationCard(d, '')}
      <div class="actions" style="margin-bottom:15px">
        ${d.status === 'accepted' ? '<button class="btn" data-act="status" data-status="picked_up">Mark picked up</button>' : ''}
        ${['accepted', 'picked_up'].includes(d.status) ? '<button class="btn" data-act="status" data-status="completed">Mark completed</button>' : ''}
        ${open ? '<button class="btn danger" data-act="status" data-status="cancelled">Cancel donation</button>' : ''}
      </div>
      <h2>Claim requests</h2>
      ${reqs.length ? reqs.map(r => `<div class="card"><div class="card-row"><b>${esc(r.recipientName)}</b>${badge(r.status)}</div>
        <div class="muted">${fmtDate(r.createdAt)} ${r.recipientPhone ? '· ☎ ' + esc(r.recipientPhone) : ''}</div>
        <p>${esc(r.message || '(no message)')}</p>
        ${r.status === 'pending' ? `<div class="actions"><button class="btn small" data-act="accept" data-id="${r.id}">Accept</button>
          <button class="btn small danger" data-act="reject" data-id="${r.id}">Reject</button></div>` : ''}</div>`).join('')
        : '<p class="muted">No requests yet.</p>'}`;
    el.onclick = e => guard(async () => {
      const t = e.target.closest('[data-act]');
      if (!t) return;
      if (t.dataset.act === 'status') {
        if (t.dataset.status === 'cancelled' && !confirm('Cancel this donation?')) return;
        await api('donations/' + id + '/status', 'POST', { status: t.dataset.status });
        toast('Donation updated.');
      } else {
        await api('requests/' + t.dataset.id + '/' + t.dataset.act, 'POST');
        toast(t.dataset.act === 'accept' ? 'Request accepted.' : 'Request rejected.');
      }
      donorViews.donation(el, id);
    });
  },

  schedule: viewSchedule,
  emergency: viewEmergency,
  notifications: viewNotifications
};

startApp('donor', donorViews, 'dashboard');
