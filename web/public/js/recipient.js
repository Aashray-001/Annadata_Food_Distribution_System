/* Recipient dashboard */
const recipientViews = {
  async dashboard(el) {
    const [avail, claims, aids] = await Promise.all([api('donations/available'), api('my/claims'), api('emergencies')]);
    el.innerHTML = `<h1>Recipient Dashboard &amp; Available Aid</h1>
      <div class="stats">${statCard('🍲 Available donations', avail.length, '#2e7d32')}${statCard('📋 My claims', claims.length, '#1565c0')}${statCard('🚨 Active emergencies', aids.length, '#c62828')}</div>
      <h2>Recently available food</h2>
      ${avail.length ? avail.slice(0, 5).map(d => donationCard(d, `<button class="btn small" data-act="claim" data-id="${d.id}" data-title="${esc(d.title)}">Claim food</button>`)).join('')
        : '<p class="muted">No available food donations at the moment. Check back soon!</p>'}`;
    bindClaim(el, () => recipientViews.dashboard(el));
  },

  async browse(el) {
    const all = await api('donations/available');
    el.innerHTML = `<h1>🔍 Browse Available Food</h1>
      <div class="toolbar"><input id="q" placeholder="Search by title or location…">
        <select id="cat"><option value="">All categories</option>${CATS_R.map(c => `<option value="${c}">${nice(c)}</option>`).join('')}</select></div>
      <div id="list"></div>`;
    const render = () => {
      const q = $('#q').value.toLowerCase().trim(), cat = $('#cat').value;
      const rows = all.filter(d => (!q || d.title.toLowerCase().includes(q) || (d.pickupAddress || '').toLowerCase().includes(q)) && (!cat || d.category === cat));
      $('#list').innerHTML = rows.length ? rows.map(d => donationCard(d, `<button class="btn small" data-act="claim" data-id="${d.id}" data-title="${esc(d.title)}">Claim food</button>`)).join('')
        : '<p class="muted">No donations match your search.</p>';
    };
    $('#q').oninput = render; $('#cat').onchange = render;
    render();
    bindClaim(el, () => recipientViews.browse(el));
  },

  async claims(el) {
    const rows = await api('my/claims');
    el.innerHTML = `<h1>📋 My Food Claims</h1>` + table(['Request', 'Donation', 'Donor', 'My message', 'Status'],
      rows.map(r => ['#' + r.id, esc(r.donationTitle), esc(r.donorName), esc(r.message), badge(r.status)]));
  },

  emergency: viewEmergency,
  schedule: viewSchedule,
  notifications: viewNotifications
};

const CATS_R = ['cooked', 'raw', 'packaged', 'beverages', 'dog_food', 'other'];

function bindClaim(el, refresh) {
  el.onclick = e => guard(async () => {
    const t = e.target.closest('[data-act="claim"]');
    if (!t) return;
    const msg = prompt('Claim: ' + t.dataset.title + '\nAdd an optional message or pickup note for the donor:');
    if (msg === null) return;
    await api('donations/' + t.dataset.id + '/claim', 'POST', { message: msg });
    toast('Donation claim submitted! The donor will review your request.');
    refresh();
  });
}

startApp('recipient', recipientViews, 'dashboard');
