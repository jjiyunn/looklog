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
                    alert('로그인이 필요합니다.');
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




// 옷장 모달
let currentBoardId = null;
let currentBtn = null;

const overlay = document.getElementById('drawer-modal-overlay');
const listEl = document.getElementById('drawer-modal-list');

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
        .catch(msg => alert(msg));
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

            if (saved) {
                closeDrawerModal();
            }
        })
        .catch(msg => alert(msg));
}

// 새 서랍 만들기
document.getElementById('new-drawer-btn').addEventListener('click', () => {
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
            loadDrawerList(); // 목록 새로고침
        })
        .catch(msg => alert(msg));
});

document.getElementById('drawer-modal-close').addEventListener('click', closeDrawerModal);
overlay.addEventListener('click', (e) => {
    if (e.target === overlay) closeDrawerModal();
});

// 옷장 클릭
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
            if (drawerId == null) return; // 방어 코드: 이미 없는 상태면 그냥 무시

            return fetch(`/drawer/${drawerId}/board/${boardId}`, { method: 'POST' })
                .then(res => res.ok ? res.json() : Promise.reject('오류가 발생했습니다.'))
                .then(saved => {
                    btn.classList.toggle('saved', saved); // saved는 false로 옴 → 클래스 제거됨
                });
        })
        .catch(msg => alert(msg));
}










// ===============================
// upload-modal / 피드 수정
// ===============================
const uploadModal = document.getElementById('upload-modal');
const uploadOpenBtn = document.getElementById('upload-open-btn');
const uploadCloseBtn = document.getElementById('upload-close');
const uploadImageInput = document.getElementById('upload-image-input');
const uploadPreview = document.getElementById('upload-preview');
const uploadPlaceholder = document.querySelector('.upload-placeholder');
const uploadSubmitBtn = document.getElementById('upload-submit-btn');

let uploadMode = 'create';
let editingBoardId = null;

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
    }
});

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
                alert(uploadMode === 'edit' ? '수정되었습니다!' : '게시글이 등록되었습니다!');
                closeUploadModal();
                location.reload();
            } else {
                return res.text().then(msg => {
                    errorBox.textContent = msg;
                    errorBox.style.display = 'block';
                });
            }
        });
});

// 이미지 드래그 앤 드롭
const uploadPreviewBox = document.querySelector('.upload-preview-box');

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
            alert('이미지 파일만 업로드할 수 있어요.');
            return;
        }

        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(file);
        uploadImageInput.files = dataTransfer.files;

        uploadImageInput.dispatchEvent(new Event('change'));
    });
}

// 태그 선택
const tagOptions = document.querySelectorAll('.tag-option');
const uploadTagsInput = document.getElementById('upload-tags');
const selectedTagsPreview = document.getElementById('selected-tags-preview');
let selectedTags = [];

tagOptions.forEach(btn => {
    btn.addEventListener('click', () => {
        const tag = btn.dataset.tag;

        if (selectedTags.includes(tag)) {
            selectedTags = selectedTags.filter(t => t !== tag);
            btn.classList.remove('selected');
        } else {
            if (selectedTags.length >= 6) {
                alert('태그는 최대 6개까지 선택할 수 있어요.');
                return;
            }
            selectedTags.push(tag);
            btn.classList.add('selected');
        }

        uploadTagsInput.value = selectedTags.join(',');
    });
});

// 컬러 태그에 실제 색상 적용
const colorMap = {
    '블랙': '#000000',
    '화이트': '#ffffff',
    '브라운': '#492a16',
    '블루': '#325ada',
    '그레이': '#8f8f8f',
    '핑크': '#ea8aaa',
    '그린': '#338737',
    '레드': '#d5322e',
    '베이지': '#e8d2b5',
    '네이비': '#122247',
    '옐로우': '#f4d136',
    '퍼플': '#653ecd',
    '카키': '#263926',
    '오렌지': '#e58435'
};

document.querySelectorAll('.tag-color-list .tag-option').forEach(btn => {
    const tagName = btn.dataset.tag;
    const color = colorMap[tagName];

    if (color) {
        btn.style.setProperty('--tag-color', color);
        btn.classList.add('tag-color-swatch');
    }
});