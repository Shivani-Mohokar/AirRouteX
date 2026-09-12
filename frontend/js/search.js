const sourceSelect = document.getElementById('source');
const destinationSelect = document.getElementById('destination');
const searchForm = document.getElementById('searchForm');
const optimizeOptions = document.getElementById('optimizeOptions');
const searchMsg = document.getElementById('searchMsg');

(async function init() {
  const user = await requireLogin();
  if (!user) return;

  try {
    const airports = await api.get('airports');
    const optionsHtml = airports
      .map((a) => `<option value="${a.code}">${a.city} (${a.code})</option>`)
      .join('');
    sourceSelect.innerHTML = optionsHtml;
    destinationSelect.innerHTML = optionsHtml;
    if (airports.length > 1) {
      destinationSelect.selectedIndex = 1;
    }
  } catch (err) {
    searchMsg.textContent = 'Could not load airports: ' + err.message;
    searchMsg.classList.add('show');
  }
})();

optimizeOptions.addEventListener('change', () => {
  [...optimizeOptions.querySelectorAll('label')].forEach((label) => {
    const input = label.querySelector('input');
    label.classList.toggle('active', input.checked);
  });
});

searchForm.addEventListener('submit', (e) => {
  e.preventDefault();
  searchMsg.classList.remove('show');

  const source = sourceSelect.value;
  const destination = destinationSelect.value;
  const type = searchForm.querySelector('input[name="type"]:checked').value;

  if (source === destination) {
    searchMsg.textContent = 'Source and destination cannot be the same city.';
    searchMsg.classList.add('show');
    return;
  }

  const params = new URLSearchParams({ source, destination, optimizationType: type });
  window.location.href = `results.html?${params.toString()}`;
});
