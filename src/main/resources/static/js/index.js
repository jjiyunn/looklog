// PANEL SCROLL
const sections = document.querySelectorAll(".panel");

function update() {
  const scrollY = window.scrollY;

  sections.forEach((sec, i) => {

    const isLast = i === sections.length - 1;

    const top = sec.offsetTop;
    const height = sec.offsetHeight;

    let p = (scrollY - top) / height;
    p = Math.max(0, Math.min(1, p));

    if (isLast) {
      sec.style.transform = "translateY(0px)";
      sec.style.zIndex = i;
      return;
    }

    const translateY = -p * 80;

    sec.style.transform = `translateY(${translateY}px)`;
    sec.style.zIndex = i;
  });
}

window.addEventListener("scroll", update);
update();


// ===============================
// modal
// ===============================
const modal = document.getElementById('modal');
const openBtns = document.querySelectorAll('.login-open');
const joinOpenBtns = document.querySelectorAll('.join-open');
const closeBtn = document.querySelector('.close');

const loginForm = document.querySelector('.modal-login');
const joinForm = document.querySelector('.modal-join');
const goJoinBtn = document.querySelector('.go-join');
const goLoginBtn = document.querySelector('.go-login');

const agreeCheckbox = document.getElementById('agree-terms');
const signupBtn = document.querySelector('.join-btn');

// login
openBtns.forEach(btn => {
  btn.addEventListener('click', () => {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
    loginForm.hidden = false;
    joinForm.hidden = true;
  });
});

// join
joinOpenBtns.forEach(btn => {
  btn.addEventListener('click', () => {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
    loginForm.hidden = true;
    joinForm.hidden = false;
  });
});


// close
function closeModal() {
  modal.classList.remove('active');
  document.body.style.overflow = '';
  resetModalForms();
}

closeBtn.addEventListener('click', closeModal);

modal.addEventListener('click', (e) => {
  if (e.target === modal) {
    closeModal();
  }
});

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    closeModal();
  }
});


// login <-> join
goJoinBtn.addEventListener('click', (e) => {
  e.preventDefault();
  loginForm.hidden = true;
  joinForm.hidden = false;
});

goLoginBtn.addEventListener('click', (e) => {
  e.preventDefault();
  joinForm.hidden = true;
  loginForm.hidden = false;
});

function resetModalForms() {
  joinForm.hidden = true;
  loginForm.hidden = false;
}


// 약관
if (agreeCheckbox && signupBtn) {
  signupBtn.disabled = true;

  agreeCheckbox.addEventListener('change', () => {
    signupBtn.disabled = !agreeCheckbox.checked;
  });
}

// 회원가입
const EMAIL_PATTERN = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]).{8,20}$/;
const NAME_PATTERN = /^[a-zA-Z0-9_.]{6,12}$/;

const joinEmail = document.getElementById('join-email');
const joinPassword = document.getElementById('join-password');
const joinPasswordCheck = document.getElementById('join-password-check');
const joinName = document.getElementById('join-name');
const joinUsername = document.getElementById('join-username');
const agreeTerms = document.getElementById('agree-terms');

const emailError = document.getElementById('email-error');
const passwordError = document.getElementById('password-error');
const passwordCheckError = document.getElementById('password-check-error');
const nameError = document.getElementById('name-error');
const usernameError = document.getElementById('username-error');
const agreeError = document.getElementById('agree-error');

function setFieldError(el, message) {
  if (!el) {
    console.log('setFieldError: el이 null임, message:', message);
    return;
  }
  if (message) {
    el.textContent = message;
    el.classList.add('show');
  } else {
    el.textContent = '';
    el.classList.remove('show');
  }
}

function validateEmail() {
  const msg = !joinEmail.value ? '이메일을 입력해주세요.'
      : !EMAIL_PATTERN.test(joinEmail.value) ? '이메일 형식이 올바르지 않습니다.' : '';
  setFieldError(emailError, msg);
  return !msg;
}

function validatePassword() {
  const msg = !joinPassword.value ? '비밀번호를 입력해주세요.'
      : !PASSWORD_PATTERN.test(joinPassword.value) ? '8~20자, 영문/숫자/특수문자를 포함해야 합니다.' : '';
  setFieldError(passwordError, msg);
  return !msg;
}

function validatePasswordCheck() {
  const msg = !joinPasswordCheck.value ? '비밀번호를 다시 입력해주세요.'
      : joinPassword.value !== joinPasswordCheck.value ? '비밀번호가 일치하지 않습니다.' : '';
  setFieldError(passwordCheckError, msg);
  return !msg;
}

function validateName() {
  const msg = !joinName.value ? '이름을 입력해주세요.'
      : !NAME_PATTERN.test(joinName.value) ? '6~12자의 영문, 숫자, _, . 만 사용 가능합니다.' : '';
  setFieldError(nameError, msg);
  return !msg;
}

function validateUsername() {
  const msg = !joinUsername.value ? '사용자이름을 입력해주세요.'
      : !NAME_PATTERN.test(joinUsername.value) ? '6~12자의 영문, 숫자, _, . 만 사용 가능합니다.' : '';
  setFieldError(usernameError, msg);
  return !msg;
}

function validateAgree() {
  console.log('validateAgree 실행됨, checked:', agreeTerms.checked); // 디버깅용
  const msg = !agreeTerms.checked ? '약관에 동의해주세요.' : '';
  setFieldError(agreeError, msg);
  return !msg;
}

// ===== 실시간 검증 =====
if (joinEmail) joinEmail.addEventListener('input', validateEmail);
if (joinPassword) joinPassword.addEventListener('input', () => { validatePassword(); validatePasswordCheck(); });
if (joinPasswordCheck) joinPasswordCheck.addEventListener('input', validatePasswordCheck);
if (joinName) joinName.addEventListener('input', validateName);
if (joinUsername) joinUsername.addEventListener('input', validateUsername);
if (agreeTerms) agreeTerms.addEventListener('change', validateAgree);

// ===== 제출 시 최종 검증 =====
const joinSubmitBtn = document.getElementById('join-submit-btn');
console.log('join-submit-btn 찾음?', joinSubmitBtn); // 디버깅용

if (joinSubmitBtn) {
  joinSubmitBtn.addEventListener('click', function () {
    console.log('제출 클릭됨'); // 디버깅용

    const emailOk = validateEmail();
    const passwordOk = validatePassword();
    const passwordCheckOk = validatePasswordCheck();
    const nameOk = validateName();
    const usernameOk = validateUsername();
    const agreeOk = validateAgree();

    console.log({ emailOk, passwordOk, passwordCheckOk, nameOk, usernameOk, agreeOk }); // 디버깅용

    if (!emailOk || !passwordOk || !passwordCheckOk || !nameOk || !usernameOk || !agreeOk) {
      return;
    }

    fetch('/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        email: joinEmail.value,
        password: joinPassword.value,
        name: joinName.value,
        userName: joinUsername.value
      })
    })
        .then(res => {
          if (res.ok) {
            window.location.href = '/looklog';
          } else {
            return res.text().then(msg => {
              setSignupServerError(msg);
            });
          }
        });
  });
}

function setSignupServerError(msg) {
  if (msg.includes('이메일')) {
    setFieldError(emailError, msg);
  } else if (msg.includes('비밀번호')) {
    setFieldError(passwordError, msg);
  } else if (msg.includes('이름') || msg.includes('사용자이름')) {
    setFieldError(usernameError, msg);
  } else {
    setFieldError(emailError, msg); // 어느 것도 안 걸리면 기본 자리
  }
}


//로그인
document.getElementById('login-submit-btn').addEventListener('click', function () {
  const email = document.getElementById('login-email').value;
  const password = document.getElementById('login-password').value;
  const errorBox = document.getElementById('login-error');

  // 프론트 단 기본 검증
  if (!email || !password) {
    errorBox.textContent = '이메일과 비밀번호를 입력해주세요.';
    errorBox.style.display = 'block';
    return;
  }

  // 서버로 전송
  fetch('/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({ email, password })
  })
      .then(res => {
        if (res.ok) {
          window.location.href = '/looklog';   // ← reload 대신 이동
        } else {
          return res.text().then(msg => {
            errorBox.textContent = msg;
            errorBox.style.display = 'block';
          });
        }
      });
});

// 로그아웃
const logoutBtn = document.getElementById('logout-btn');
if (logoutBtn) {
  logoutBtn.addEventListener('click', function () {
    fetch('/logout', { method: 'POST' })
        .then(() => {
          location.reload();
        });
  });
}

// LOOKLOG(HOME)
const looklogBtn = document.getElementById('looklog-btn');
if (looklogBtn) {
  looklogBtn.addEventListener('click', function () {
    window.location.href = '/looklog';
  });
}



//PaperCrop Img Change
// document.addEventListener('DOMContentLoaded', () => {
//   const wrap = document.querySelector('.ll-paperCrop');
//   const imgs = {
//     모자: wrap.querySelector('img[src*="모자"]'),
//     상의: wrap.querySelector('img[src*="상의"]'),
//     하의: wrap.querySelector('img[src*="하의"]'),
//     신발: wrap.querySelector('img[src*="신발"]'),
//     악세: wrap.querySelector('img[src*="악세"]'),
//   };

//   // 어울리는 조합을 미리 세트로 정의 (원하는 만큼 추가 가능)
//   const looks = [
//     { 모자: 1, 상의: 1, 하의: 1, 신발: 1, 악세: 1 },
//     { 모자: 2, 상의: 2, 하의: 2, 신발: 2, 악세: 2 },
//     { 모자: 3, 상의: 3, 하의: 3, 신발: 33, 악세: 3 },
//     { 모자: 4, 상의: 4, 하의: 4, 신발: 4, 악세: 4 },
//   ];

//   let current = 0;

//   function scheduleNext() {
//     const delay = 5000; // 5~8초 사이 랜덤

//     setTimeout(() => {
//       // 전체 세트 동시에 페이드아웃
//       Object.values(imgs).forEach(img => img.classList.add('is-swapping'));

//       setTimeout(() => {
//         current = (current + 1) % looks.length;
//         const look = looks[current];

//         Object.entries(imgs).forEach(([category, img]) => {
//           const folder = img.src.substring(0, img.src.lastIndexOf('/') + 1);
//           img.src = `${folder}${category}${look[category]}.png`;
//         });

//         requestAnimationFrame(() => {
//           Object.values(imgs).forEach(img => img.classList.remove('is-swapping'));
//         });

//         scheduleNext();
//       }, 350);
//     }, delay);
//   }

//   scheduleNext();
// });





// ===============================
// 3d
// ===============================
const model = document.querySelector('#jacketModel');

let angle = -20;
let direction = 1;
const speed = 0.15;

function rotateJacket() {
  angle += speed * direction;

  if (angle >= 20) {
    angle = 20;
    direction = -1;
  }

  if (angle <= -20) {
    angle = -20;
    direction = 1;
  }

  model.cameraOrbit = `${angle}deg 75deg 105%`;

  requestAnimationFrame(rotateJacket);
}

rotateJacket();






//OOTD DATE
const dateEl = document.querySelector('.ll-ootd-date span');

const today = new Date();

const month = String(today.getMonth() + 1).padStart(2, '0');
const day = String(today.getDate()).padStart(2, '0');

dateEl.textContent = `${month} / ${day}`;