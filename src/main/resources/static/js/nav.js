$(function(){

  $("#title").on("click", function (e) {
    e.preventDefault();

    $("html, body").animate(
      {
        scrollTop: 0,
      },
      500 // 0.5s
    );
  });

  $(function () {
    $("#title").on("click", function (e) {
      e.preventDefault();
      $("html, body").animate({ scrollTop: 0 }, 500);
    });

    const THRESHOLD = 50;

    function getTitleStates() {
      const target = document.querySelector("#target");
      const title = document.querySelector("#title");
      const targetRect = target.getBoundingClientRect();
      const targetTop = targetRect.top + (targetRect.height / 2) - (title.offsetHeight / 2);

      const windowWidth = window.innerWidth;
      const initialFontSize = windowWidth * 0.08;
      const targetScale = 6 / initialFontSize;

      return { topExpanded: 120, topCollapsed: targetTop, scaleExpanded: 1, scaleCollapsed: targetScale };
    }

    function applyHeaderState(scrolled) {
      const title = document.querySelector("#title");
      const { topExpanded, topCollapsed, scaleExpanded, scaleCollapsed } = getTitleStates();

      title.style.top = `${scrolled ? topCollapsed : topExpanded}px`;
      title.style.transform = `translateX(-50%) scale(${scrolled ? scaleCollapsed : scaleExpanded})`;


      $("body").toggleClass("is-scrolled", scrolled);

      $(".hero-search").css({
        opacity: scrolled ? 0 : 1,
        transform: `translateX(-50%) translateY(${scrolled ? -133 : 0}px)`
      });

      $(".oo-line").css({
        opacity: scrolled ? 0 : 1,
        transform: `translateX(-50%) translateY(${scrolled ? -570 : 0}px)`
      });

      $("header p").css({
        opacity: scrolled ? 0 : 1,
        transform: `translateX(-50%) translateY(${scrolled ? -400 : 0}px)`
      });

      $(".oo-nav button").css({
        opacity: scrolled ? 1 : 0,
        transform: `translateX(${scrolled ? 190 : 0}%)`
      });

      $("header").css({
        background: scrolled ? "rgba(250,248,242,0.9)" : ""
      });

      if (!scrolled) {
        $(".nav-search-box").removeClass("open");
        $(".nav-search-btn i").removeClass("ti-x").addClass("ti-search");
      }
    }


    let isScrolled = null; // false 대신 null

    function checkScrollState() {
      const sb = $(window).scrollTop();
      const shouldBeScrolled = sb > THRESHOLD;

      if (shouldBeScrolled !== isScrolled) {
        isScrolled = shouldBeScrolled;
        applyHeaderState(isScrolled);
      }

      const tagWrapEl = document.querySelector(".oo-tag-wrap");
      if (tagWrapEl) {
        const tagWrapTop = tagWrapEl.getBoundingClientRect().top;
        $(".oo-tag-bg").toggleClass("is-stuck", tagWrapTop <= 70);
      }
    }

    $(window).on("scroll", checkScrollState);
    $(window).on("resize", function () {
      applyHeaderState(isScrolled); // 리사이즈되면 target 위치 다시 계산해서 재적용
    });

    checkScrollState();
  });

  // $(window).trigger("scroll");




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
