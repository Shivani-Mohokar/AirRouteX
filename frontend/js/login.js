const loginForm = document.getElementById('loginForm');
const loginMsg = document.getElementById('loginMsg');

loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  loginMsg.classList.remove('show');

  const username = document.getElementById('username').value;
  const password = document.getElementById('password').value;

  try {
    const user = await api.post('login', { username, password });
    window.location.href = user.role === 'ADMIN' ? 'admin.html' : 'search.html';
  } catch (err) {
    loginMsg.textContent = err.message;
    loginMsg.classList.add('show');
  }
});
