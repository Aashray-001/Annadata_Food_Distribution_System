/* Login + register page */
const loginForm = $('#loginForm'), registerForm = $('#registerForm');

function showTab(login) {
  loginForm.classList.toggle('hidden', !login);
  registerForm.classList.toggle('hidden', login);
  $('#tabLogin').classList.toggle('active', login);
  $('#tabRegister').classList.toggle('active', !login);
}
$('#tabLogin').onclick = () => showTab(true);
$('#tabRegister').onclick = () => showTab(false);

// already logged in? go straight to the dashboard
api('auth/me').then(me => { location.href = me.user.role.toLowerCase() + '.html'; }).catch(() => {});

loginForm.onsubmit = async ev => {
  ev.preventDefault();
  $('#loginError').textContent = '';
  try {
    const r = await api('auth/login', 'POST', formJson(loginForm));
    location.href = r.redirect;
  } catch (e) {
    $('#loginError').textContent = e.message;
  }
};

registerForm.onsubmit = async ev => {
  ev.preventDefault();
  const err = $('#registerError');
  err.textContent = '';
  const data = formJson(registerForm);
  if (data.password !== data.confirm) { err.textContent = 'Passwords do not match.'; return; }
  delete data.confirm;
  try {
    await api('auth/register', 'POST', data);
    registerForm.reset();
    showTab(true);
    $('#loginError').style.color = '#2e7d32';
    $('#loginError').textContent = 'Registration successful! Please log in.';
    setTimeout(() => { $('#loginError').style.color = ''; }, 4000);
  } catch (e) {
    err.textContent = e.message;
  }
};
