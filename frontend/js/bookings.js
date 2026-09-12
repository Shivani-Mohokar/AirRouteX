const bookingsArea = document.getElementById('bookingsArea');

function formatDuration(minutes) {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${h}h ${m}m`;
}

async function loadBookings() {
  try {
    const bookings = await api.get('bookings');

    if (bookings.length === 0) {
      bookingsArea.innerHTML = `<div class="empty-state">No bookings yet. <a href="search.html">Search for a flight</a> to get started.</div>`;
      return;
    }

    bookingsArea.innerHTML = bookings.map((b) => renderBooking(b)).join('');

    bookings.forEach((b) => {
      const btn = document.getElementById(`cancel-${b.bookingId}`);
      if (btn) {
        btn.addEventListener('click', () => cancelBooking(b.bookingId));
      }
    });
  } catch (err) {
    bookingsArea.innerHTML = `<div class="error-state">${err.message}</div>`;
  }
}

function renderBooking(b) {
  const routeCities = [b.flights[0].sourceCode, ...b.flights.map((f) => f.destinationCode)];
  const statusClass = b.status === 'CONFIRMED' ? 'confirmed' : 'cancelled';

  return `
    <div class="card">
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <h3>Booking #${b.bookingId}</h3>
        <span class="status-badge ${statusClass}">${b.status}</span>
      </div>

      <div class="route-summary">
        <div class="stat">
          <div class="value">${Number(b.totalDistanceKm).toLocaleString('en-IN')} km</div>
          <div class="label">Distance</div>
        </div>
        <div class="stat">
          <div class="value">&#8377;${Number(b.totalPrice).toLocaleString('en-IN')}</div>
          <div class="label">Total Cost</div>
        </div>
        <div class="stat">
          <div class="value">${formatDuration(b.totalDurationMinutes)}</div>
          <div class="label">Travel Time</div>
        </div>
        <div class="stat">
          <div class="value">${b.numberOfLayovers}</div>
          <div class="label">Layovers</div>
        </div>
      </div>

      <div class="route-path">
        ${routeCities
          .map((c, i) => `<span class="city">${c}</span>${i < routeCities.length - 1 ? '<span class="arrow">&rarr;</span>' : ''}`)
          .join('')}
      </div>

      <p class="flight-id">Booked ${b.bookingDate} &middot; Optimized for ${b.optimizationType.toLowerCase()}</p>

      ${b.status === 'CONFIRMED' ? `<button id="cancel-${b.bookingId}" class="btn danger small">Cancel Booking</button>` : ''}
    </div>
  `;
}

async function cancelBooking(bookingId) {
  if (!confirm('Cancel this booking?')) return;
  try {
    await api.post('bookings/cancel', { bookingId });
    await loadBookings();
  } catch (err) {
    alert(err.message);
  }
}

(async function init() {
  const user = await requireLogin();
  if (!user) return;
  await loadBookings();
})();
