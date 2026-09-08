$(function(){

  $("#title").on("click", function (e) {
    e.preventDefault();

    $("html, body").animate(
      {
        scrollTop: 0,
      },
      500 // 0.5초
    );
  });

  $(window).on("scroll", function () {

    // 최대 300px까지만 계산
    let s = Math.min($(this).scrollTop(), 400);

    const title = document.querySelector("#title");
    const target = document.querySelector("#target");

    function handleScroll() {
      const progress = Math.min(Math.max(window.scrollY / 250, 0), 1);

      const targetRect = target.getBoundingClientRect();
      const targetTop = targetRect.top + (targetRect.height / 2) - (title.offsetHeight / 2);

      const currentTop = 120 + (targetTop - 120) * progress;

      const windowWidth = window.innerWidth;
      const initialFontSize = windowWidth * 0.08; // 처음 글자 크기 (8vw)


      const preferredSize = windowWidth * 0.01; // 권장 크기 (2vw)
      const targetScale = 6 / initialFontSize; // 최종 축소 비율 계산

      const currentScale = 1 + (targetScale - 1) * progress;

      title.style.top = `${currentTop}px`;
      title.style.transform = `translateX(-50%) scale(${currentScale})`;
    }

    window.addEventListener("scroll", handleScroll);
    window.addEventListener("resize", handleScroll);
    handleScroll();


    // 검색창
    $(".hero-search").css({
      opacity: Math.max(0, 1 - s / 150),
      transform: `translateX(-50%) translateY(${-s / 3}px)`
    });

    // 파란 선
    $(".oo-line").css({
      opacity: Math.max(0, 1 - s / 150),
      width: `${60 - s / 4}%`,
      transform: `translateX(-50%) translateY(${-s / 0.7}px)`
    });

    // 설명 글
    $("header p").css({
      opacity: Math.max(0, 1 - s / 100),
      transform: `translateX(-50%) translateY(${-s / 1}px)`
    });


    const sb = $(window).scrollTop();

    // oo-tag bg
    const tagWrapEl = document.querySelector(".oo-tag-wrap");
    if (tagWrapEl) {
      const tagWrapTop = tagWrapEl.getBoundingClientRect().top;
      if (tagWrapTop <= 70) {
        $(".oo-tag-bg").addClass("is-stuck");
      } else {
        $(".oo-tag-bg").removeClass("is-stuck");
      }
    }


    const progress = Math.min(Math.max((sb - 200) / 150, 0), 1);

    $(".oo-nav button").css({
      opacity: progress,
      transform: `translateX(${progress * 190}%)`
    });

    
    if (sb < 300) {

      $(".nav-search-box").removeClass("open");

      $(".nav-search-btn i")
        .removeClass("ti-x")
        .addClass("ti-search");

    }

    $("header").css({
      background: `rgba(250,248,242,${progress * 0.9})`
    });

  });

  $(window).trigger("scroll");




  //tag
  const $slider = $(".oo-tag");

  let isDown = false;
  let isDragging = false;
  let startX;
  let scrollLeft;

  $slider.on("pointerdown", function (e) {
    isDown = true;
    isDragging = false;

    startX = e.clientX;
    scrollLeft = this.scrollLeft;
  });

  $(document).on("pointerup", function () {
    isDown = false;
  });

  $slider.on("pointermove", function (e) {
    if (!isDown) return;

    const walk = e.clientX - startX;

    // 5px 이상 움직이면 드래그로 판단
    if (Math.abs(walk) > 5) {
      isDragging = true;
    }

    this.scrollLeft = scrollLeft - walk;
  });

  $(".oo-tag a").on("click", function (e) {
    if (isDragging) {
      e.preventDefault();
      e.stopImmediatePropagation();
    }
  });
});
