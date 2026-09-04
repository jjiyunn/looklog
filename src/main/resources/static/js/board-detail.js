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

// 신고
const reportBtn = document.getElementById('report-board-btn');
if (reportBtn) {
    reportBtn.addEventListener('click', function () {
        alert('신고가 접수되었습니다.');
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
                    countEl.textContent = `${newCount} 팔로워`;
                }
            })
            .catch(msg => alert(msg));
    });
}









