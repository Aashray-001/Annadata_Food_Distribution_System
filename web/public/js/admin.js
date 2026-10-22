/* Admin dashboard */
const adminViews = {
  async overview(el) {
    const s = await api('admin/stats');
    el.innerHTML = `<h1>System Overview &amp; Live Analytics</h1>
      <div class="stats">${statCard('👥 Total users', s.users, '#2e7d32')}${statCard('🍲 Total donations', s.donations, '#1565c0')}
        ${statCard('📋 Claim requests', s.requests, '#e65100')}${statCard('🚨 Active emergencies', s.activeEmergencies, '#c62828')}</div>
      <div id="chartsHere"></div>`;
    await viewAnalytics($('#chartsHere'), true);
  },

  async users(el) {
    const rows = await api('admin/users');
    el.innerHTML = `<h1>👥 User Accounts Management</h1>` + table(['ID', 'Name', 'Email', 'Role', 'Phone', 'Status', 'Action'],
      rows.map(u => [u.id, esc(u.name), esc(u.email), esc(u.role), esc(u.phone),
        u.active ? badge('active', 'status-available') : badge('deactivated', 'status-closed'),
        u.id === ME.id ? '<span class="muted">(you)</span>'
          : `<button class="btn small ${u.active ? 'danger' : ''}" data-id="${u.id}" data-active="${!u.active}">${u.active ? 'Deactivate' : 'Activate'}</button>`]));
    el.onclick = e => guard(async () => {
      const t = e.target.closest('[data-id]');
      if (!t) return;
      await api('admin/users/' + t.dataset.id + '/active', 'POST', { active: t.dataset.active === 'true' });
      adminViews.users(el);
    });
  },

  async donations(el) {
    const rows = await api('admin/donations');
    el.innerHTML = `<h1>🍲 All Food Donations</h1>` + table(['ID', 'Title', 'Donor', 'Category', 'Source type', 'Source name', 'Quantity', 'Expiry', 'Status'],
      rows.map(d => [d.id, esc(d.title), esc(d.donorName), esc(nice(d.category)), esc(d.sourceType), esc(d.sourceName),
        esc(d.quantity + ' ' + d.unit), fmtDate(d.expiryDate), badge(d.status)]));
  },

  async requests(el) {
    const rows = await api('admin/requests');
    el.innerHTML = `<h1>📋 Food Claim Requests</h1>` + table(['ID', 'Donation', 'Recipient', 'Message', 'Date', 'Status'],
      rows.map(r => [r.id, esc(r.donationTitle) + ' <span class="muted">(#' + r.donationId + ')</span>', esc(r.recipientName), esc(r.message), fmtDate(r.createdAt), badge(r.status)]));
  },

  analytics: viewAnalytics,
  schedule: viewSchedule,
  places: viewPlaces,
  emergency: viewEmergency,
  notifications: viewNotifications
};

startApp('admin', adminViews, 'overview');
