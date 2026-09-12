/**
 * api.js
 * ------------------------------------------------------------------
 * Shared fetch helper used by every page's JS file.
 *
 * IMPORTANT: this project uses server-side Java HttpSession
 * authentication (a JSESSIONID cookie), NOT a token in headers.
 * The browser sends that cookie automatically on every same-origin
 * request, so there is nothing to attach manually here - we just
 * need `credentials: 'same-origin'` (the default, set explicitly
 * for clarity) so the cookie is always included.
 *
 * Requests are sent as "application/x-www-form-urlencoded" (plain
 * form data), matching how the servlets read parameters via
 * request.getParameter(...) - no JSON body parsing needed on the
 * backend at all.
 * ------------------------------------------------------------------
 */

async function apiRequest(path, { method = 'GET', params = null } = {}) {
  let url = path;
  const options = {
    method,
    credentials: 'same-origin',
  };

  if (method === 'GET' || method === 'DELETE') {
    if (params) {
      const query = new URLSearchParams(params).toString();
      url += (path.includes('?') ? '&' : '?') + query;
    }
  } else {
    options.headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
    options.body = new URLSearchParams(params || {}).toString();
  }

  const res = await fetch(url, options);

  let data = {};
  try {
    data = await res.json();
  } catch {
    // empty body - ignore
  }

  if (!res.ok) {
    throw new Error(data.error || `Request failed (${res.status})`);
  }

  return data;
}

const api = {
  get: (path, params) => apiRequest(path, { method: 'GET', params }),
  post: (path, params) => apiRequest(path, { method: 'POST', params }),
  put: (path, params, queryParams) => {
    const query = queryParams ? '?' + new URLSearchParams(queryParams).toString() : '';
    return apiRequest(path + query, { method: 'PUT', params });
  },
  del: (path, params) => apiRequest(path, { method: 'DELETE', params }),
};

/** Fetches the current session user, or null if not logged in. Never throws. */
async function getCurrentUser() {
  try {
    return await api.get('current-user');
  } catch {
    return null;
  }
}

/** Redirects to login.html if nobody is logged in. Returns the user if they are. */
async function requireLogin() {
  const user = await getCurrentUser();
  if (!user) {
    window.location.href = 'login.html';
    return null;
  }
  return user;
}

/** Redirects non-admins away. Returns the user if they ARE an admin. */
async function requireAdmin() {
  const user = await requireLogin();
  if (user && user.role !== 'ADMIN') {
    window.location.href = 'search.html';
    return null;
  }
  return user;
}
