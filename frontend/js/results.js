const resultsArea = document.getElementById('resultsArea');

const MODE_LABELS = {
  distance: 'Shortest Distance Route (Dijkstra)',
  price: 'Lowest Price Route (Dijkstra)',
  duration: 'Shortest Duration Route (Dijkstra)',
  layovers: 'Minimum Layovers Route (BFS)',
};

function formatDuration(minutes) {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${h}h ${m}m`;
}

function getParams() {
  const p = new URLSearchParams(window.location.search);
  return {
    source: p.get('source'),
    destination: p.get('destination'),
    optimizationType: p.get('optimizationType'),
  };
}

function renderRoute(data, params) {
  const flights = data.flights;
  const routeCities = [flights[0].sourceCode, ...flights.map((f) => f.destinationCode)];

  const pathHtml = routeCities
    .map((city, i) => {
      const arrow = i < routeCities.length - 1 ? '<span class="arrow">&rarr;</span>' : '';
      return `<span class="city">${city}</span>${arrow}`;
    })
    .join('');

  const segmentsHtml = flights
    .map(
      (f) => `
      <div class="segment">
        <div>
          <strong>${f.sourceCode} &rarr; ${f.destinationCode}</strong><br/>
          <span class="flight-id">${f.airlineName} ${f.flightNumber} &middot; ${f.departureTime} - ${f.arrivalTime}</span>
        </div>
        <div style="text-align:right;">
          &#8377;${Number(f.price).toLocaleString('en-IN')}<br/>
          <span class="flight-id">${f.distanceKm} km &middot; ${formatDuration(f.durationMinutes)}</span>
        </div>
      </div>`
    )
    .join('');

  resultsArea.innerHTML = `
    <div class="card">
      <h3>${MODE_LABELS[data.optimizationType] || data.optimizationType}</h3>

      <div class="route-summary">
        <div class="stat">
          <div class="value">${Number(data.totalDistanceKm).toLocaleString('en-IN')} km</div>
          <div class="label">Total Distance</div>
        </div>
        <div class="stat">
          <div class="value">&#8377;${Number(data.totalPrice).toLocaleString('en-IN')}</div>
          <div class="label">Total Cost</div>
        </div>
        <div class="stat">
          <div class="value">${formatDuration(data.totalDurationMinutes)}</div>
          <div class="label">Total Travel Time</div>
        </div>
        <div class="stat">
          <div class="value">${data.numberOfLayovers}</div>
          <div class="label">Layovers</div>
        </div>
      </div>

      <div class="route-path">${pathHtml}</div>

      <h3 style="margin-top:24px;">Flight Segments</h3>
      ${segmentsHtml}

      <div id="bookMsg" class="msg" style="margin-top: 16px;"></div>
      <button id="bookBtn" class="btn" style="margin-top: 8px;">Book This Route</button>
    </div>
  `;

  document.getElementById('bookBtn').addEventListener('click', async () => {
    const bookMsg = document.getElementById('bookMsg');
    const bookBtn = document.getElementById('bookBtn');
    bookBtn.disabled = true;
    bookMsg.className = 'msg';

    try {
      const result = await api.post('bookings', params);
      bookMsg.textContent = `Booked! Confirmation #${result.bookingId}. See it on your Booking History page.`;
      bookMsg.classList.add('success', 'show');
    } catch (err) {
      bookMsg.textContent = err.message;
      bookMsg.classList.add('error', 'show');
      bookBtn.disabled = false;
    }
  });
}

(async function init() {
  const user = await requireLogin();
  if (!user) return;

  const params = getParams();

  if (!params.source || !params.destination || !params.optimizationType) {
    resultsArea.innerHTML = `<div class="error-state">Missing search parameters. Please search again.</div>`;
    return;
  }

  try {
    const data = await api.get('search', params);
    renderRoute(data, params);
  } catch (err) {
    resultsArea.innerHTML = `<div class="error-state">${err.message}</div>`;
  }
})();
