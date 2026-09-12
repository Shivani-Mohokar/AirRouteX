/**
 * nav.js
 * ------------------------------------------------------------------
 * Renders the shared navbar into <div id="navbar"></div> on every
 * page, adapting its links to whether someone is logged in (and
 * whether they're an admin) by calling GET /current-user.
 * ------------------------------------------------------------------
 */

async function renderNavbar() {
  const navbarEl = document.getElementById('navbar');
  if (!navbarEl) return;

  const user = await getCurrentUser();

  let linksHtml = `<a href="index.html">Home</a>`;

  if (user) {
    linksHtml += `<a href="search.html">Search Flights</a>`;
    linksHtml += `<a href="bookings.html">Booking History</a>`;
    if (user.role === 'ADMIN') {
      linksHtml += `<a href="admin.html">Admin Panel</a>`;
    }
    linksHtml += `<span class="role-badge">${user.role}</span>`;
    linksHtml += `<button class="link-btn" id="logoutBtn">Logout (${user.username})</button>`;
  } else {
    linksHtml += `<a href="login.html">Login</a>`;
    linksHtml += `<a href="register.html">Register</a>`;
  }

  navbarEl.innerHTML = `
    <div class="brand"><a href="index.html" style="color:inherit;">Air<span>RouteX</span></a></div>
    <nav>${linksHtml}</nav>
  `;

  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', async () => {
      await api.post('logout');
      window.location.href = 'login.html';
    });
  }
}

document.addEventListener('DOMContentLoaded', renderNavbar);
