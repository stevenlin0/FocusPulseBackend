let focusMins = 25;
let breakMins = 5;
let isRunning = false;
let isFocus = true;
let timeLeft = focusMins * 60;
let interval;
let sessionStart = null;
let sessions = 0;
let streak = 0;
let totalTime = 0;

const timerEl = document.getElementById('timer-display');
const startBtn = document.getElementById('start-pause-btn');
const resetBtn = document.getElementById('reset-btn');
const statusText = document.getElementById('status-text');
const completedSessions = document.getElementById('completed-sessions');
const focusTime = document.getElementById('focus-time');
const currentStreak = document.getElementById('current-streak');
const focusInput = document.getElementById('focus-duration');
const breakInput = document.getElementById('break-duration');

// Utility
function pad(num) {
  return num < 10 ? '0' + num : num;
}

function setControlState(isTimerRunning) {
  focusInput.disabled = isTimerRunning;
  breakInput.disabled = isTimerRunning;
  document.getElementById('save-settings-btn').disabled = isTimerRunning;
  document.getElementById('clear-history-btn').disabled = isTimerRunning;
  document.getElementById('show-history-btn').disabled = isTimerRunning;
}

function updateDisplay() {
  const min = Math.floor(timeLeft / 60);
  const sec = timeLeft % 60;
  timerEl.textContent = `${pad(min)}:${pad(sec)}`;
}

function updateStats() {
  completedSessions.textContent = sessions;
  focusTime.textContent = `${Math.floor(totalTime / 3600)}h ${Math.floor((totalTime % 3600) / 60)}m`;
  currentStreak.textContent = streak;
  document.getElementById('session-count').textContent = sessions;
}

function getTimeString(date) {
  return date.toTimeString().slice(0, 5);
}

function getDateString(date) {
  return date.toISOString().split('T')[0];
}

// Fetch settings from backend
async function fetchSettings() {
  try {
    const res = await fetch('/api/settings');
    const data = await res.json();

    focusMins = data.focus_minutes;
    breakMins = data.break_minutes;

    // Populate dropdowns
    focusInput.value = focusMins;
    breakInput.value = breakMins;

    timeLeft = focusMins * 60;
    updateDisplay();
  } catch (err) {
    console.error('Failed to fetch settings:', err);
  }
}

// Save session
async function saveSession(startTime, duration, type) {
  const endTime = new Date();
  const payload = {
    session_date: getDateString(startTime),
    start_time: getTimeString(startTime),
    end_time: getTimeString(endTime),
    duration: duration,
    type: type
  };

  try {
    await fetch('/api/sessions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    console.log('Session saved:', payload);
  } catch (err) {
    console.error('Failed to save session:', err);
  }
}

function finishSession() {
  clearInterval(interval);
  isRunning = false;
  setControlState(false); // Re-enable controls

  const duration = isFocus ? focusMins : breakMins;
  if (isFocus) {
    sessions++;
    totalTime += focusMins * 60;
    streak++;
  } else {
    streak = 0;
  }

  saveSession(sessionStart, duration, isFocus ? 'work' : 'break');

  startBtn.textContent = '▶ Start';
  isFocus = !isFocus;
  timeLeft = (isFocus ? focusMins : breakMins) * 60;
  statusText.textContent = isFocus ? 'Focus Session' : 'Break Time';
  updateDisplay();
  updateStats();
}


// Event listeners
startBtn.onclick = () => {
   if (isRunning) {
     clearInterval(interval);
     startBtn.textContent = '▶ Start';
     isRunning = false;
     setControlState(false); // Re-enable controls
   } else {
     sessionStart = new Date();
     startBtn.textContent = '⏸ Pause';
     isRunning = true;
     setControlState(true); // Disable controls

     interval = setInterval(() => {
       timeLeft--;
       updateDisplay();
       if (timeLeft <= 0) finishSession();
     }, 1000);
   }
 };


resetBtn.onclick = () => {
  clearInterval(interval);
  isRunning = false;
  startBtn.textContent = '▶ Start';
  setControlState(false);
  timeLeft = (isFocus ? focusMins : breakMins) * 60;
  updateDisplay();
};


document.getElementById('clear-history-btn').onclick = () => {
  sessions = 0;
  streak = 0;
  totalTime = 0;
  updateStats();
};

focusInput.onchange = (e) => {
  focusMins = parseInt(e.target.value);
  if (isFocus && !isRunning) {
    timeLeft = focusMins * 60;
    updateDisplay();
  }
};

breakInput.onchange = (e) => {
  breakMins = parseInt(e.target.value);
  if (!isFocus && !isRunning) {
    timeLeft = breakMins * 60;
    updateDisplay();
  }
};

document.getElementById('save-settings-btn').onclick = async () => {
  const focusValue = parseInt(document.getElementById('focus-duration').value);
  const breakValue = parseInt(document.getElementById('break-duration').value);

  if (!focusValue || !breakValue || focusValue < 1 || breakValue < 1) {
    alert('Please enter valid durations for focus and break.');
    return;
  }

  try {
    await fetch('/api/settings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        focus_minutes: focusValue,
        break_minutes: breakValue
      })
    });

    alert('Settings saved!');
    focusMins = focusValue;
    breakMins = breakValue;

    // Reset current timer if not running
    if (!isRunning) {
      timeLeft = (isFocus ? focusMins : breakMins) * 60;
      updateDisplay();
    }
  } catch (err) {
    console.error('Failed to save settings:', err);
    alert('Failed to save settings.');
  }
};


// Init
fetchSettings();
updateDisplay();
updateStats();
