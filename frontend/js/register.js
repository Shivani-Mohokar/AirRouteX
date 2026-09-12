const registerForm = document.getElementById('registerForm');
const registerMsg = document.getElementById('registerMsg');

registerForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  registerMsg.classList.remove('show');

  const username = document.getElementById('username').value;
  const email = document.getElementById('email').value;
  const password = document.getElementById('password').value;
  const confirmPassword = document.getElementById('confirmPassword').value;

  if (password !== confirmPassword) {
    registerMsg.textContent = 'Passwords do not match';
    registerMsg.classList.add('show');
    return;
  }

  try {
    await api.post('register', { username, email, password });
    window.location.href = 'search.html';
  } catch (err) {
    registerMsg.textContent = err.message;
    registerMsg.classList.add('show');
  }
});
