$(function () {

  $("#title").on("click", function (e) {
    e.preventDefault();
    $("html, body").animate({ scrollTop: 0 }, 500);
  });

  function getThreshold() {
    const w = window.innerWidth;
    if (w <= 600) return 25;
    if (w <= 850) return 35;
    if (w <= 1400) return 55;
    return 80;
  }

  let THRESHOLD = getThreshold();
  let isScrolled = null;


  const HYSTERESIS = 25;
  const TRANSITION_MS = 600;

  let isLocked = false;
  let lockTimer = null;

  function applyHeaderState(scrolled) {
    $("header").toggleClass("is-scrolled", scrolled);
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

    if (!scrolled) {
      $(".nav-search-box").removeClass("open");
      $(".nav-search-btn i").removeClass("ti-x").addClass("ti-search");
    }
  }

  function checkScrollState() {

    if (isLocked) return;

    const sb = $(window).scrollTop();

    let shouldBeScrolled = isScrolled;

    if (isScrolled !== true && sb > THRESHOLD + HYSTERESIS) {
      shouldBeScrolled = true;
    } else if (isScrolled !== false && sb < THRESHOLD - HYSTERESIS) {
      shouldBeScrolled = false;
    } else if (isScrolled === null) {
      shouldBeScrolled = sb > THRESHOLD;
    }

    if (shouldBeScrolled !== isScrolled) {
      isScrolled = shouldBeScrolled;
      applyHeaderState(isScrolled);

      isLocked = true;
      clearTimeout(lockTimer);
      lockTimer = setTimeout(() => {
        isLocked = false;
        checkScrollState();
      }, TRANSITION_MS);
    }

    const tagWrapEl = document.querySelector(".oo-tag-wrap");
    if (tagWrapEl) {
      const tagWrapTop = tagWrapEl.getBoundingClientRect().top;
      $(".oo-tag-bg").toggleClass("is-stuck", tagWrapTop <= 70);
    }
  }

  $(window).on("scroll", checkScrollState);
  $(window).on("resize", function () {
    THRESHOLD = getThreshold();
  });
  $(window).on("load", checkScrollState);
  checkScrollState();


  // tag drag scroll
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
    if (Math.abs(walk) > 5) isDragging = true;
    this.scrollLeft = scrollLeft - walk;
  });

  $(".oo-tag a").on("click", function (e) {
    if (isDragging) {
      e.preventDefault();
      e.stopImmediatePropagation();
    }
  });

});