// ===============================
// 더보기 메뉴 (수정/삭제/신고/공유)
// ===============================
const moreMenuBtn = document.getElementById('more-menu-btn');
const moreMenuDropdown = document.getElementById('more-menu-dropdown');

if (moreMenuBtn) {
    moreMenuBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        moreMenuDropdown.hidden = !moreMenuDropdown.hidden;
    });

    document.addEventListener('click', () => {
        moreMenuDropdown.hidden = true;
    });
}

// 공유
const shareBtn = document.getElementById('share-board-btn');
if (shareBtn) {
    shareBtn.addEventListener('click', () => {
        navigator.clipboard.writeText(window.location.href)
            .then(() => alert('링크가 복사되었습니다.'));
    });
}

// 피드 삭제
const deleteBtn = document.getElementById('delete-board-btn');
if (deleteBtn) {
    deleteBtn.addEventListener('click', function () {
        if (!confirm('정말 삭제하시겠습니까?')) return;

        const boardId = this.dataset.boardId;

        fetch(`/board/${boardId}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    alert('삭제되었습니다.');
                    window.location.href = '/looklog';
                } else {
                    alert('삭제에 실패했습니다.');
                }
            });
    });
}




// ==============================
// profile only
// ==============================

// 탭 전환
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.hidden = true);

        btn.classList.add('active');
        document.getElementById('tab-' + btn.dataset.tab).hidden = false;
    });
});

// 팔로우 버튼
const followBtn = document.getElementById('follow-btn');
if (followBtn) {
    followBtn.addEventListener('click', function () {
        const targetId = this.dataset.memberId;

        fetch(`/follow/${targetId}`, { method: 'POST' })
            .then(res => res.ok ? res.json() : Promise.reject('로그인이 필요합니다.'))
            .then(following => {
                if (following) {
                    followBtn.textContent = '팔로잉';
                    followBtn.classList.add('following');
                } else {
                    followBtn.textContent = '팔로우';
                    followBtn.classList.remove('following');
                }

                // 팔로워 카운트 업데이트
                const countEl = document.getElementById('follower-count');
                if (countEl) {
                    const currentCount = parseInt(countEl.textContent, 10) || 0;
                    const newCount = following ? currentCount + 1 : currentCount - 1;
                    countEl.textContent = newCount;   // ★ 숫자만 넣기
                }
            })
            .catch(msg => alert(msg));
    });
}

const followModalOverlay = document.getElementById('follow-modal-overlay');
const followModalTitle = document.getElementById('follow-modal-title');
const followModalList = document.getElementById('follow-modal-list');

document.addEventListener('click', function (e) {
    const btn = e.target.closest('.follow-count-btn');
    if (!btn) return;

    const type = btn.dataset.type;       // 'followers' or 'followings'
    const memberId = btn.dataset.memberId;

    openFollowModal(type, memberId);
});

function openFollowModal(type, memberId) {
    followModalTitle.textContent = type === 'followers' ? '팔로워' : '팔로잉';

    fetch(`/follow/${type}/${memberId}`)
        .then(res => res.ok ? res.json() : Promise.reject('오류가 발생했습니다.'))
        .then(users => renderFollowList(users))
        .catch(msg => alert(msg));

    followModalOverlay.hidden = false;
}

function renderFollowList(users) {
    followModalList.innerHTML = '';

    users.forEach(u => {
        const li = document.createElement('li');
        li.className = 'follow-modal-item';
        li.innerHTML = `
            <a class="follow-modal-user-link" href="/profile/${u.userName}">
                <img class="follow-modal-avatar" src="${u.profileImg}" alt="">
                <span class="follow-modal-username">${u.userName}</span>
            </a>
            ${(!u.me && !u.following) ? `<button class="follow-modal-follow-btn" data-target-id="${u.id}">팔로우</button>` : ''}
        `;
        followModalList.appendChild(li);
    });
}

const followModalCloseBtn = document.getElementById('follow-modal-close');
if (followModalCloseBtn) {
    followModalCloseBtn.addEventListener('click', () => {
        followModalOverlay.hidden = true;
    });
}
if (followModalOverlay) {
    followModalOverlay.addEventListener('click', (e) => {
        if (e.target === followModalOverlay) followModalOverlay.hidden = true;
    });
}

// 모달 안에서 "팔로우" 버튼 클릭 → 팔로우 처리 후 버튼만 사라지게
document.addEventListener('click', function (e) {
    const btn = e.target.closest('.follow-modal-follow-btn');
    if (!btn) return;

    const targetId = btn.dataset.targetId;

    fetch(`/follow/${targetId}`, { method: 'POST' })
        .then(res => res.ok ? res.json() : Promise.reject('오류가 발생했습니다.'))
        .then(following => {
            if (following) {
                btn.remove();
            }
        })
        .catch(msg => alert(msg));
});


//회원 탈퇴
const withdrawBtn = document.getElementById('withdraw-btn');
const withdrawModal = document.getElementById('withdraw-modal');
const withdrawCancelBtn = document.getElementById('withdraw-cancel-btn');
const withdrawConfirmBtn = document.getElementById('withdraw-confirm-btn');
const withdrawPasswordInput = document.getElementById('withdraw-password');
const withdrawError = document.getElementById('withdraw-error');

if (withdrawBtn) {
    withdrawBtn.addEventListener('click', () => {
        document.getElementById('more-menu-dropdown').hidden = true; // 드롭다운 닫기
        withdrawModal.hidden = false;
        withdrawPasswordInput.value = '';
        withdrawError.textContent = '';
    });

    withdrawCancelBtn.addEventListener('click', () => {
        withdrawModal.hidden = true;
    });

    withdrawConfirmBtn.addEventListener('click', async () => {
        const password = withdrawPasswordInput.value;
        if (!password) {
            withdrawError.textContent = '비밀번호를 입력해주세요.';
            return;
        }

        const res = await fetch('/profile/withdraw', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `password=${encodeURIComponent(password)}`
        });

        if (res.ok) {
            window.location.replace('/');
        } else {
            const msg = await res.text();
            withdrawError.textContent = msg || '비밀번호가 일치하지 않습니다.';
        }
    });
}



// profile edit
// 파일 선택하고 바로 화면에 반영
const profileImgInput = document.getElementById('profileImg');
const profilePreview = document.getElementById('edit-profile-preview');
if (profileImgInput) {
    profileImgInput.addEventListener('change', () => {
        const file = profileImgInput.files[0];
        if (file) {
            profilePreview.src = URL.createObjectURL(file);
        }
    });
}

// 히스토리 안쌓이게
const editProfileForm = document.getElementById('edit-profile-form');
if (editProfileForm) {
    editProfileForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const formData = new FormData(editProfileForm);

        fetch('/profile/edit', {
            method: 'POST',
            body: formData
        })
            .then(res => {
                if (res.redirected) {
                    window.location.replace(res.url); // ★ back()으로 안 돌아오게
                } else {
                    return res.text().then(html => {
                        document.open();
                        document.write(html); // 에러 페이지면 그대로 표시
                        document.close();
                    });
                }
            })
            .catch(() => alert('오류가 발생했습니다.'));
    });
}
const editProfileBtn = document.getElementById('edit-profile-btn');
if (editProfileBtn) {
    editProfileBtn.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.replace('/profile/edit');
    });
}

const namePattern = /^[a-zA-Z0-9_.]{6,12}$/;

function attachValidation(inputId, hintId) {
    const input = document.getElementById(inputId);
    const hint = document.getElementById(hintId);
    if (!input) return;

    input.addEventListener('input', () => {
        const value = input.value;

        if (value.length === 0) {
            hint.textContent = '';
            input.setCustomValidity('');
            return;
        }

        if (!namePattern.test(value)) {
            hint.textContent = '영문, 숫자, _, . 만 사용 가능 (6~12자)';
            hint.classList.add('invalid');
            input.setCustomValidity('invalid');
        } else {
            hint.textContent = '';
            hint.classList.remove('invalid');
            input.setCustomValidity('');
        }
    });
}

attachValidation('name', 'nameHint');
attachValidation('userName', 'userNameHint');


