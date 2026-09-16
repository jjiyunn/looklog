

// 서랍 수정
const renameOverlay = document.getElementById('rename-modal-overlay');
const renameInput = document.getElementById('rename-drawer-input');
let renameTargetId = null;

const renameBtn = document.getElementById('rename-drawer-btn');
if (renameBtn) {
  renameBtn.addEventListener('click', () => {
    renameTargetId = renameBtn.dataset.drawerId;
    renameInput.value = document.querySelector('.drawer-detail-header h2').textContent;
    renameOverlay.hidden = false;
  });
}

document.getElementById('cancel-btn').addEventListener('click', () => {
  renameOverlay.hidden = true;
});

document.getElementById('confirm-btn').addEventListener('click', () => {
  const newName = renameInput.value.trim();
  if (!newName) return;

  fetch(`/drawer/${renameTargetId}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name: newName })
  })
      .then(res => res.ok ? location.reload() : Promise.reject('수정에 실패했습니다.'))
      .catch(msg => showToast(msg, 'error'));
});


// 서랍 삭제
const deleteDrawerBtn = document.getElementById('delete-drawer-btn');
if (deleteDrawerBtn) {
  deleteDrawerBtn.addEventListener('click', async function () {
    const ok = await showConfirm('이 서랍을 삭제할까요? 안에 담긴 목록도 함께 삭제됩니다.');
    if (!ok) return;

    const drawerId = this.dataset.drawerId;
    const loginMemberName = document.body.dataset.loginMemberName;

    fetch(`/drawer/${drawerId}`, { method: 'DELETE' })
        .then(res => res.ok ? window.location.href = '/profile/' + loginMemberName : Promise.reject('삭제에 실패했습니다.'))
        .catch(msg => showToast(msg, 'error'));
  });
}