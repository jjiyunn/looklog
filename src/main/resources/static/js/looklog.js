

const feed = document.querySelector('.feed');
const feedLoader = document.getElementById('loader');

const feedItems = [...document.querySelectorAll('.feed-item')];

const initialCount = 12;
const batchCount = 8;

let shownCount = 0;
let feedLoading = false;

let feedColumns = [];
let feedObserver = null;


// column 개수
function getColumnCount() {
  const width = window.innerWidth;

  if (width <= 750) return 2;
  if (width <= 1300) return 3;

  return 4;
}

let resizeTimer;

window.addEventListener('resize', () => {
  clearTimeout(resizeTimer);

  resizeTimer = setTimeout(() => {
    const newColumnCount = getColumnCount();

    if (newColumnCount !== feedColumns.length) {

      const shownItems = feedItems.slice(0, shownCount);
      createColumns();
      shownItems.forEach(item => addFeedItem(item));
    }
  }, 200);
});


// column 생성
function createColumns() {

  const items = [...feed.querySelectorAll('.feed-item')];

  feed.querySelectorAll('.feed-column').forEach(column => {
    column.remove();
  });

  items.forEach(item => {
    item.remove();
  });

  feedColumns = [];

  const columnCount = getColumnCount();

  for (let i = 0; i < columnCount; i++) {

    const column = document.createElement('div');

    column.className = 'feed-column';

    feed.appendChild(column);
    feedColumns.push(column);
  }
}

function getShortestColumn() {

  return feedColumns.reduce((shortest, column) => {
    return column.offsetHeight < shortest.offsetHeight
        ? column
        : shortest;
  });
}


// 게시물 추가
function addFeedItem(item) {
  item.classList.remove('feed-hidden');
  const shortestColumn = getShortestColumn();
  shortestColumn.appendChild(item);
}


// 초기 표시(12개)
function showInitialItems() {

  createColumns();

  const initialItems = feedItems.slice(
      0,
      initialCount
  );

  initialItems.forEach(item => {
    addFeedItem(item);
  });

  shownCount = initialItems.length;

  if (shownCount >= feedItems.length) {
    if (feedLoader) {
      feedLoader.textContent = '모든 게시글을 확인했어요';
    }
  }
}


// 로딩(+8개)
function showNextBatch() {

  if (feedLoading) return;
  if (shownCount >= feedItems.length) return;

  feedLoading = true;

  const nextItems = feedItems.slice(
      shownCount,
      shownCount + batchCount
  );

  nextItems.forEach(item => {
    addFeedItem(item);
  });

  shownCount += nextItems.length;

  if (shownCount >= feedItems.length) {
    if (feedLoader) {
      feedLoader.textContent = '모든 게시글을 확인했어요';
    }
  }

  feedLoading = false;
}



// 이미지 로딩 기다리기
function waitForImages() {

  const images = feedItems
      .map(item => item.querySelector('.feed-img'))
      .filter(Boolean);

  return Promise.all(

      images.map(img => {
        if (img.complete) {
          return Promise.resolve();
        }
        return new Promise(resolve => {
          img.addEventListener(
              'load',
              resolve,
              { once: true }
          );
          img.addEventListener(
              'error',
              resolve,
              { once: true }
          );
        });
      })
  );
}


// IntersectionObserver
function setupFeedObserver() {

  if (!feedLoader) return;
  if (feedObserver) {
    feedObserver.disconnect();
  }

  feedObserver = new IntersectionObserver(

      entries => {
        if (!entries[0].isIntersecting) {
          return;
        }
        showNextBatch();
      },

      {
        root: null,
        rootMargin: '800px 0px',
        threshold: 0
      }
  );
  feedObserver.observe(feedLoader);
}


// 시작
async function initFeed() {
  await waitForImages();
  showInitialItems();
  setupFeedObserver();
}

initFeed();



// sort
$(".oo-sort-toggle p").on("click", function () {
  const isPopular = $(this).text() === "인기순";

  $(".oo-sort-toggle p").css("color", "#5474bb");
  $(this).css("color", "#f5f5f5");

  if (isPopular) {
    $(".oo-sort-indicator").css("left", "83px"); // 값 조절해야댐
  } else {
    $(".oo-sort-indicator").css("left", "7px");
  }

  // 정렬 기준 바꿔서 다시 불러오기
  const sort = isPopular ? "popular" : "latest";
  window.location.href = `/looklog?sort=${sort}`;
});