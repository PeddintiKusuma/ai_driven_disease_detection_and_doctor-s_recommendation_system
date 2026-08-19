const DAY_TO_JS = { sun: 0, mon: 1, tue: 2, wed: 3, thu: 4, fri: 5, sat: 6 };

export function parseDoctorAvailableDays(availableDaysStr) {
  if (!availableDaysStr) return [];
  return availableDaysStr.split(',').map((d) => {
    const key = d.trim().toLowerCase().substring(0, 3);
    return DAY_TO_JS[key];
  }).filter((v) => v !== undefined);
}

export function getNextAvailableDates(availableDaysStr, count = 10) {
  const allowedDays = parseDoctorAvailableDays(availableDaysStr);
  if (allowedDays.length === 0) return [];

  const dates = [];
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  for (let i = 0; i < 60 && dates.length < count; i++) {
    const d = new Date(today);
    d.setDate(today.getDate() + i);
    if (allowedDays.includes(d.getDay())) {
      dates.push(d.toISOString().split('T')[0]);
    }
  }
  return dates;
}

export function formatDisplayDate(dateStr) {
  const d = new Date(dateStr + 'T00:00:00');
  return d.toLocaleDateString('en-IN', { weekday: 'short', day: 'numeric', month: 'short' });
}
