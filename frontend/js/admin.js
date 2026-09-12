// ---- Tab switching ----
document.querySelectorAll('.tab-btn').forEach((btn) => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.tab-btn').forEach((b) => b.classList.remove('active'));
    document.querySelectorAll('.tab-panel').forEach((p) => p.classList.remove('active'));
    btn.classList.add('active');
    document.getElementById(btn.dataset.tab).classList.add('active');
  });
});

function showMsg(el, message, type) {
  el.textContent = message;
  el.className = `msg show ${type}`;
  setTimeout(() => el.classList.remove('show'), 3500);
}

// ================= AIRPORTS =================

const airportForm = document.getElementById('airportForm');
const airportMsg = document.getElementById('airportMsg');
const airportsTableBody = document.querySelector('#airportsTable tbody');

let cachedAirports = [];

async function loadAirports() {
  cachedAirports = await api.get('admin/airports');

  airportsTableBody.innerHTML = cachedAirports
    .map(
      (a) => `
      <tr>
        <td>${a.code}</td>
        <td>${a.city}</td>
        <td>${a.name}</td>
        <td><button class="btn danger small" onclick="deleteAirport(${a.airportId})">Delete</button></td>
      </tr>`
    )
    .join('');

  const optionsHtml = cachedAirports
    .map((a) => `<option value="${a.airportId}">${a.city} (${a.code})</option>`)
    .join('');
  document.getElementById('flightSource').innerHTML = optionsHtml;
  document.getElementById('flightDestination').innerHTML = optionsHtml;
}

airportForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  try {
    await api.post('admin/airports', {
      code: document.getElementById('airportCode').value.trim().toUpperCase(),
      city: document.getElementById('airportCity').value.trim(),
      name: document.getElementById('airportName').value.trim(),
    });
    airportForm.reset();
    showMsg(airportMsg, 'Airport added successfully.', 'success');
    await loadAirports();
  } catch (err) {
    showMsg(airportMsg, err.message, 'error');
  }
});

async function deleteAirport(id) {
  if (!confirm('Delete this airport? Related flights will also be removed.')) return;
  try {
    await api.del('admin/airports', { id });
    await loadAirports();
    await loadFlights();
  } catch (err) {
    alert(err.message);
  }
}

// ================= AIRLINES =================

const airlineForm = document.getElementById('airlineForm');
const airlineMsg = document.getElementById('airlineMsg');
const airlinesTableBody = document.querySelector('#airlinesTable tbody');

async function loadAirlines() {
  const airlines = await api.get('admin/airlines');

  airlinesTableBody.innerHTML = airlines
    .map((a) => `<tr><td>${a.code}</td><td>${a.name}</td></tr>`)
    .join('');

  const optionsHtml = airlines.map((a) => `<option value="${a.airlineId}">${a.name} (${a.code})</option>`).join('');
  document.getElementById('flightAirline').innerHTML = optionsHtml;
}

airlineForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  try {
    await api.post('admin/airlines', {
      code: document.getElementById('airlineCode').value.trim().toUpperCase(),
      name: document.getElementById('airlineName').value.trim(),
    });
    airlineForm.reset();
    showMsg(airlineMsg, 'Airline added successfully.', 'success');
    await loadAirlines();
  } catch (err) {
    showMsg(airlineMsg, err.message, 'error');
  }
});

// ================= FLIGHTS =================

const flightForm = document.getElementById('flightForm');
const flightMsg = document.getElementById('flightMsg');
const flightsTableBody = document.querySelector('#flightsTable tbody');
const flightSubmitBtn = document.getElementById('flightSubmitBtn');
const flightCancelEdit = document.getElementById('flightCancelEdit');
const flightFormTitle = document.getElementById('flightFormTitle');

async function loadFlights() {
  const flights = await api.get('admin/flights');

  flightsTableBody.innerHTML = flights
    .map(
      (f) => `
      <tr>
        <td>${f.flightNumber}</td>
        <td>${f.sourceCode} &rarr; ${f.destinationCode}</td>
        <td>${f.distanceKm} km</td>
        <td>&#8377;${Number(f.price).toLocaleString('en-IN')}</td>
        <td>${f.durationMinutes} min</td>
        <td style="white-space:nowrap;">
          <button class="btn small" onclick='editFlight(${JSON.stringify(f)})'>Edit</button>
          <button class="btn danger small" onclick="deleteFlight(${f.flightId})">Delete</button>
        </td>
      </tr>`
    )
    .join('');
}

flightForm.addEventListener('submit', async (e) => {
  e.preventDefault();

  const payload = {
    flightNumber: document.getElementById('flightNumber').value.trim(),
    airlineId: document.getElementById('flightAirline').value,
    sourceAirportId: document.getElementById('flightSource').value,
    destinationAirportId: document.getElementById('flightDestination').value,
    distanceKm: document.getElementById('flightDistance').value,
    price: document.getElementById('flightPrice').value,
    durationMinutes: document.getElementById('flightDuration').value,
    departureTime: document.getElementById('flightDeparture').value,
    arrivalTime: document.getElementById('flightArrival').value,
  };

  const editingId = document.getElementById('flightId').value;

  try {
    if (editingId) {
      await api.put('admin/flights', payload, { id: editingId });
      showMsg(flightMsg, 'Flight updated successfully.', 'success');
    } else {
      await api.post('admin/flights', payload);
      showMsg(flightMsg, 'Flight added successfully.', 'success');
    }
    resetFlightForm();
    await loadFlights();
  } catch (err) {
    showMsg(flightMsg, err.message, 'error');
  }
});

function editFlight(flight) {
  document.getElementById('flightId').value = flight.flightId;
  document.getElementById('flightNumber').value = flight.flightNumber;
  document.getElementById('flightAirline').value = flight.airlineId;
  document.getElementById('flightSource').value = flight.sourceAirportId;
  document.getElementById('flightDestination').value = flight.destinationAirportId;
  document.getElementById('flightDistance').value = flight.distanceKm;
  document.getElementById('flightPrice').value = flight.price;
  document.getElementById('flightDuration').value = flight.durationMinutes;
  document.getElementById('flightDeparture').value = flight.departureTime.slice(0, 5);
  document.getElementById('flightArrival').value = flight.arrivalTime.slice(0, 5);

  flightSubmitBtn.textContent = 'Update Flight';
  flightFormTitle.textContent = 'Edit Flight';
  flightCancelEdit.style.display = 'inline-block';
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function resetFlightForm() {
  flightForm.reset();
  document.getElementById('flightId').value = '';
  flightSubmitBtn.textContent = 'Add Flight';
  flightFormTitle.textContent = 'Add New Flight';
  flightCancelEdit.style.display = 'none';
}

flightCancelEdit.addEventListener('click', resetFlightForm);

async function deleteFlight(id) {
  if (!confirm('Delete this flight?')) return;
  try {
    await api.del('admin/flights', { id });
    await loadFlights();
  } catch (err) {
    alert(err.message);
  }
}

// ================= INIT =================

(async function init() {
  const user = await requireAdmin();
  if (!user) return;

  await loadAirports();
  await loadAirlines();
  await loadFlights();
})();
