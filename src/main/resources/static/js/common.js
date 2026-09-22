//search box click
$(".oo-nav button").on("click", function (e) {

    e.preventDefault();

    $(".nav-search-box").toggleClass("open");

    const icon = $(this).find("i");

    if ($(".nav-search-box").hasClass("open")) {
        icon.removeClass("ti-search").addClass("ti-x");
        $(".nav-search-box input").focus();
    } else {
        icon.removeClass("ti-x").addClass("ti-search");
    }
})

// 뒤로가기
document.addEventListener('click', function (e) {
    const btn = e.target.closest('.back-btn');
    if (!btn) return;

    if (window.history.length > 1) {
        window.history.back();
    } else {
        window.location.href = '/';
    }
});


// 좋아요
document.querySelectorAll('.like-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        const boardId = this.dataset.boardId;

        fetch(`/like/${boardId}`, { method: 'POST' })
            .then(res => {
                if (res.ok) {
                    return res.json();
                } else {
                    showToast('로그인이 필요합니다.', 'error');
                }
            })
            .then(liked => {
                const countSpan = btn.querySelector('.like-count');
                let count = parseInt(countSpan.textContent);

                if (liked) {
                    btn.classList.add('liked');
                    countSpan.textContent = count + 1;
                } else {
                    btn.classList.remove('liked');
                    countSpan.textContent = count - 1;
                }
            });
    });
});



// ===============================
// 옷장 모달 (drawer-modal-overlay 있는 페이지에서만 작동)
// ===============================
const overlay = document.getElementById('drawer-modal-overlay');
const listEl = document.getElementById('drawer-modal-list');

if (overlay) {
    let currentBoardId = null;
    let currentBtn = null;

    function openDrawerModal(boardId, btn) {
        currentBoardId = boardId;
        currentBtn = btn;
        loadDrawerList();
        overlay.hidden = false;
    }

    function closeDrawerModal() {
        overlay.hidden = true;
        currentBoardId = null;
        currentBtn = null;
    }

    function loadDrawerList() {
        fetch(`/drawer/list?boardId=${currentBoardId}`)
            .then(res => res.ok ? res.json() : Promise.reject('로그인이 필요합니다.'))
            .then(drawers => renderDrawerList(drawers))
            .catch(msg => showToast(msg, 'error'));
    }

    function renderDrawerList(drawers) {
        listEl.innerHTML = '';

        drawers.forEach(d => {
            const li = document.createElement('li');
            li.className = 'drawer-modal-item' + (d.saved ? ' saved' : '');
            li.dataset.drawerId = d.id;
            li.innerHTML = `
                <span class="drawer-modal-item-name">${d.name}</span>
                <span class="drawer-modal-item-count">(${d.itemCount})</span>
                <span class="drawer-modal-item-check material-symbols-rounded">check</span>
            `;
            li.addEventListener('click', () => toggleDrawerItem(d.id, li));
            listEl.appendChild(li);
        });
    }

    function toggleDrawerItem(drawerId, li) {
        fetch(`/drawer/${drawerId}/board/${currentBoardId}`, { method: 'POST' })
            .then(res => res.ok ? res.json() : Promise.reject('오류가 발생했습니다.'))
            .then(saved => {
                li.classList.toggle('saved', saved);

                const countEl = li.querySelector('.drawer-modal-item-count');
                const current = parseInt(countEl.textContent, 10) || 0;
                const newCount = saved ? current + 1 : Math.max(current - 1, 0);
                countEl.textContent = `(${newCount})`;

                currentBtn.classList.toggle('saved', saved);

                const drawerName = li.querySelector('.drawer-modal-item-name').textContent;

                if (saved) {
                    showToast(`'${drawerName}'에 저장되었습니다.`);
                    closeDrawerModal();
                }

            })
            .catch(msg => showToast(msg, 'error'));
    }

    const newDrawerBtn = document.getElementById('new-drawer-btn');
    if (newDrawerBtn) {
        newDrawerBtn.addEventListener('click', () => {
            const nameInput = document.getElementById('new-drawer-name');
            const name = nameInput.value.trim();
            if (!name) return;

            fetch('/drawer', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name })
            })
                .then(res => res.ok ? res.json() : Promise.reject('오류가 발생했습니다.'))
                .then(drawerId => {
                    nameInput.value = '';
                    loadDrawerList();
                })
                .catch(msg => showToast(msg, 'error'));
        });
    }

    const drawerModalCloseBtn = document.getElementById('drawer-modal-close');
    if (drawerModalCloseBtn) {
        drawerModalCloseBtn.addEventListener('click', closeDrawerModal);
    }

    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) closeDrawerModal();
    });

    // 옷장 클릭 (전역이지만 overlay 있는 페이지에서만 의미있음)
    document.addEventListener('click', function (e) {
        const btn = e.target.closest('.closet-btn');
        if (!btn) return;

        const boardId = btn.dataset.boardId;

        if (btn.classList.contains('saved')) {
            removeSavedItem(boardId, btn);
        } else {
            openDrawerModal(boardId, btn);
        }
    });

    function removeSavedItem(boardId, btn) {
        fetch(`/drawer/saved?boardId=${boardId}`)
            .then(res => res.ok ? res.json() : Promise.reject('오류가 발생했습니다.'))
            .then(drawerId => {
                if (drawerId == null) return;

                return fetch(`/drawer/${drawerId}/board/${boardId}`, { method: 'POST' })
                    .then(res => res.ok ? res.json() : Promise.reject('오류가 발생했습니다.'))
                    .then(saved => {
                        btn.classList.toggle('saved', saved);
                        showToast('서랍에서 제거되었습니다.');
                    });
            })
            .catch(msg => showToast(msg, 'error'));
    }
}


// ===============================
// upload-modal / 피드 수정 (upload-modal 있는 페이지에서만 작동)
// ===============================
const uploadModal = document.getElementById('upload-modal');

if (uploadModal) {
    const uploadOpenBtn = document.getElementById('upload-open-btn');
    const uploadCloseBtn = document.getElementById('upload-close');
    const uploadImageInput = document.getElementById('upload-image-input');
    const uploadPreview = document.getElementById('upload-preview');
    const uploadPlaceholder = document.querySelector('.upload-placeholder');
    const uploadSubmitBtn = document.getElementById('upload-submit-btn');
    const uploadPreviewBox = document.querySelector('.upload-preview-box');
    const tagOptions = document.querySelectorAll('.tag-option');
    const uploadTagsInput = document.getElementById('upload-tags');

    let uploadMode = 'create';
    let editingBoardId = null;
    let selectedTags = [];

    if (uploadOpenBtn) {
        uploadOpenBtn.addEventListener('click', () => {
            openUploadModal('create');
        });
    }

    const editBoardBtn = document.getElementById('edit-board-btn');
    if (editBoardBtn) {
        editBoardBtn.addEventListener('click', () => {
            openUploadModal('edit', {
                boardId: editBoardBtn.dataset.boardId,
                content: editBoardBtn.dataset.content,
                img: editBoardBtn.dataset.img,
                tags: editBoardBtn.dataset.tags
            });
        });
    }

    function openUploadModal(mode, data) {
        uploadMode = mode;
        resetUploadForm();

        if (mode === 'edit') {
            editingBoardId = data.boardId;
            uploadSubmitBtn.textContent = '수정하기';

            uploadPreview.src = data.img;
            uploadPreview.hidden = false;
            uploadPlaceholder.hidden = true;

            document.getElementById('upload-content').value = data.content || '';

            const existingTags = data.tags ? data.tags.split(',') : [];
            selectedTags = existingTags.filter(t => t);
            uploadTagsInput.value = selectedTags.join(',');
            tagOptions.forEach(btn => {
                btn.classList.toggle('selected', selectedTags.includes(btn.dataset.tag));
            });
        } else {
            editingBoardId = null;
            uploadSubmitBtn.textContent = '게시하기';
        }

        uploadModal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }

    function closeUploadModal() {
        uploadModal.classList.remove('active');
        document.body.style.overflow = '';
        resetUploadForm();
    }

    if (uploadCloseBtn) {
        uploadCloseBtn.addEventListener('click', closeUploadModal);
    }

    uploadModal.addEventListener('click', (e) => {
        if (e.target === uploadModal) {
            closeUploadModal();
        }
    });

    uploadImageInput.addEventListener('change', () => {
        const file = uploadImageInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                uploadPreview.src = e.target.result;
                uploadPreview.hidden = false;
                uploadPlaceholder.hidden = true;
            };
            reader.readAsDataURL(file);

            suggestTagsFromImage(file);
        }
    });

    function suggestTagsFromImage(file) {
        // 기존 선택된 태그 초기화
        selectedTags = [];
        uploadTagsInput.value = '';
        tagOptions.forEach(btn => btn.classList.remove('selected'));

        const formData = new FormData();
        formData.append('image', file);

        uploadPreviewBox.classList.add('ai-loading');

        fetch('/board/suggest-tags', {
            method: 'POST',
            body: formData
        })
            .then(res => res.json())
            .then(suggestedTags => {
                suggestedTags.forEach(tag => {
                    const btn = document.querySelector(`.tag-option[data-tag="${tag}"]`);
                    if (btn && !selectedTags.includes(tag) && selectedTags.length < 6) {
                        btn.click();
                    }
                });
            })
            .catch(() => {})
            .finally(() => {
                uploadPreviewBox.classList.remove('ai-loading');
            });
    }

    function resetUploadForm() {
        uploadImageInput.value = '';
        uploadPreview.src = '';
        uploadPreview.hidden = true;
        uploadPlaceholder.hidden = false;
        document.getElementById('upload-content').value = '';
        document.getElementById('upload-error').style.display = 'none';

        selectedTags = [];
        uploadTagsInput.value = '';
        tagOptions.forEach(btn => btn.classList.remove('selected'));
    }

    uploadSubmitBtn.addEventListener('click', function () {
        const file = uploadImageInput.files[0];
        const content = document.getElementById('upload-content').value;
        const tags = document.getElementById('upload-tags').value;
        const errorBox = document.getElementById('upload-error');

        if (uploadMode === 'create' && !file) {
            errorBox.textContent = '사진을 선택해주세요.';
            errorBox.style.display = 'block';
            return;
        }
        if (!tags) {
            errorBox.textContent = '1개 이상의 태그를 선택해주세요.';
            errorBox.style.display = 'block';
            return;
        }

        const formData = new FormData();
        if (file) formData.append('image', file);
        formData.append('content', content);
        formData.append('tags', tags);

        const url = uploadMode === 'edit' ? `/board/${editingBoardId}/edit` : '/board/create';

        fetch(url, {
            method: 'POST',
            body: formData
        })
            .then(res => {
                if (res.ok) {
                    showToast(uploadMode === 'edit' ? '수정되었습니다!' : '게시글이 등록되었습니다!');
                    closeUploadModal();
                    setTimeout(() => location.reload(), 800);
                } else {
                    return res.text().then(msg => {
                        errorBox.textContent = msg;
                        errorBox.style.display = 'block';
                    });
                }
            });
    });

    if (uploadPreviewBox) {
        uploadPreviewBox.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadPreviewBox.classList.add('drag-over');
        });

        uploadPreviewBox.addEventListener('dragleave', () => {
            uploadPreviewBox.classList.remove('drag-over');
        });

        uploadPreviewBox.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadPreviewBox.classList.remove('drag-over');

            const file = e.dataTransfer.files[0];
            if (!file || !file.type.startsWith('image/')) {
                showToast('이미지 파일만 업로드할 수 있어요.', 'error');
                return;
            }

            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(file);
            uploadImageInput.files = dataTransfer.files;

            uploadImageInput.dispatchEvent(new Event('change'));
        });
    }

    tagOptions.forEach(btn => {
        btn.addEventListener('click', () => {
            const tag = btn.dataset.tag;

            if (selectedTags.includes(tag)) {
                selectedTags = selectedTags.filter(t => t !== tag);
                btn.classList.remove('selected');
            } else {
                if (selectedTags.length >= 6) {
                    showToast('태그는 최대 6개까지 선택할 수 있어요.', 'error');
                    return;
                }
                selectedTags.push(tag);
                btn.classList.add('selected');
            }

            uploadTagsInput.value = selectedTags.join(',');
        });
    });

    const colorMap = {
        '블랙': '#000000', '화이트': '#ffffff', '브라운': '#492a16', '블루': '#325ada',
        '그레이': '#8f8f8f', '핑크': '#ea8aaa', '그린': '#338737', '레드': '#d5322e',
        '베이지': '#e8d2b5', '네이비': '#122247', '옐로우': '#f4d136', '퍼플': '#653ecd',
        '카키': '#263926', '오렌지': '#e58435'
    };

    document.querySelectorAll('.tag-color-list .tag-option').forEach(btn => {
        const tagName = btn.dataset.tag;
        const color = colorMap[tagName];

        if (color) {
            btn.style.setProperty('--tag-color', color);
            btn.classList.add('tag-color-swatch');
        }
    });
}

// 내용 글자 수 제한
const contentTextarea = document.getElementById('upload-content');
const contentCount = document.getElementById('content-count');

if (contentTextarea) {
    contentTextarea.addEventListener('input', () => {
        contentCount.textContent = `${contentTextarea.value.length}/300`;
    });
}


// ===============================
// report modal
// ===============================
const reportModalOverlay = document.getElementById('report-modal-overlay');

if (reportModalOverlay) {
    let reportTargetType = null;
    let reportTargetId = null;

    document.addEventListener('click', function (e) {
        const btn = e.target.closest('#report-board-btn, #report-member-btn');
        if (!btn) return;

        if (btn.id === 'report-board-btn') {
            reportTargetType = 'BOARD';
            reportTargetId = btn.dataset.targetId;
        } else {
            reportTargetType = 'MEMBER';
            reportTargetId = btn.dataset.memberId;
        }

        reportModalOverlay.hidden = false;
    });

    document.getElementById('report-modal-close').addEventListener('click', () => {
        reportModalOverlay.hidden = true;
    });

    reportModalOverlay.addEventListener('click', (e) => {
        if (e.target === reportModalOverlay) reportModalOverlay.hidden = true;
    });

    document.getElementById('report-submit-btn').addEventListener('click', () => {
        const selectedReason = document.querySelector('input[name="report-reason"]:checked');
        const detail = document.getElementById('report-detail').value;
        const errorBox = document.getElementById('report-error');

        if (!selectedReason) {
            errorBox.textContent = '신고 사유를 선택해주세요.';
            errorBox.style.display = 'block';
            return;
        }

        const formData = new URLSearchParams();
        formData.append('targetType', reportTargetType);
        formData.append('targetId', reportTargetId);
        formData.append('reason', selectedReason.value);
        formData.append('detail', detail);

        fetch('/report', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData
        })
            .then(res => res.ok ? Promise.resolve() : res.text().then(msg => Promise.reject(msg)))
            .then(() => {
                showToast('신고가 접수되었습니다.');
                reportModalOverlay.hidden = true;
                document.querySelectorAll('input[name="report-reason"]').forEach(r => r.checked = false);
                document.getElementById('report-detail').value = '';
            })
            .catch(msg => showToast(msg, 'error'));
    });
}
// 신고 상세보기
document.addEventListener('click', function (e) {
    const cell = e.target.closest('.detail-cell');
    if (!cell) return;

    cell.classList.toggle('expanded');
});


//toast
function showToast(message, type = 'default', duration = 2500) {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = 'toast' + (type === 'error' ? ' toast-error' : '');
    toast.textContent = message;

    container.appendChild(toast);

    requestAnimationFrame(() => {
        toast.classList.add('show');
    });

    setTimeout(() => {
        toast.classList.remove('show');
        toast.addEventListener('transitionend', () => toast.remove(), { once: true });
    }, duration);
}


// 더보기 메뉴
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


// confirm 삭제 모달
function showConfirm(message) {
    return new Promise(resolve => {
        const overlay = document.getElementById('confirm-modal-overlay');
        if (!overlay) {
            resolve(window.confirm(message)); // 모달 없는 페이지 대비 폴백
            return;
        }

        document.getElementById('confirm-modal-message').textContent = message;
        overlay.hidden = false;

        const okBtn = document.getElementById('confirm-modal-ok');
        const cancelBtn = document.getElementById('confirm-modal-cancel');

        function cleanup(result) {
            overlay.hidden = true;
            okBtn.removeEventListener('click', onOk);
            cancelBtn.removeEventListener('click', onCancel);
            overlay.removeEventListener('click', onOverlayClick);
            resolve(result);
        }

        function onOk() { cleanup(true); }
        function onCancel() { cleanup(false); }
        function onOverlayClick(e) {
            if (e.target === overlay) cleanup(false);
        }

        okBtn.addEventListener('click', onOk);
        cancelBtn.addEventListener('click', onCancel);
        overlay.addEventListener('click', onOverlayClick);
    });
}


// 비공개
const toggleDrawerVisibilityBtn = document.getElementById('toggle-drawer-visibility-btn');
if (toggleDrawerVisibilityBtn) {
    toggleDrawerVisibilityBtn.addEventListener('click', async function () {
        const drawerId = this.dataset.drawerId;
        const isPublic = this.dataset.public === 'true';

        const confirmed = await showConfirm(
            isPublic ? '이 서랍을 비공개로 전환할까요?' : '이 서랍을 공개로 전환할까요?'
        );
        if (!confirmed) return;

        try {
            const res = await fetch(`/drawer/${drawerId}/visibility`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ drawerPublic: !isPublic })
            });
            if (!res.ok) throw new Error();

            this.dataset.public = String(!isPublic);
            this.querySelector('span').textContent = !isPublic ? '비공개로 전환' : '공개로 전환';
            showToast(!isPublic ? '서랍을 공개로 전환했습니다.' : '서랍을 비공개로 전환했습니다.');
        } catch (e) {
            showToast('처리 중 오류가 발생했습니다.');
        }
    });
}










