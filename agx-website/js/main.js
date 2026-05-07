/* ============================================================
   AGX · ASCENDA DIGITAL CAPITAL GROUP — Main Script
   Particles · Globe · GSAP · City Popups · CMS
   ============================================================ */

(function () {
  'use strict';

  /* ─── WASM SETUP ─── */
  var wasmReady = false;
  var wasmExports, wasmMem;

  fetch('agx-globe.wasm')
    .then(function (r) { return r.arrayBuffer(); })
    .then(function (buf) {
      return WebAssembly.instantiate(buf, {
        env: {
          seed: function () { return Math.random(); },
          sin: Math.sin,
          cos: Math.cos,
          sqrt: Math.sqrt,
          pow: Math.pow,
          fmod: function (a, b) { return a % b; },
          fmin: Math.min,
          fmax: Math.max,
          log: Math.log
        }
      });
    })
    .then(function (obj) {
      wasmExports = obj.instance.exports;
      wasmMem = wasmExports.memory;
      wasmReady = true;
      if (typeof wasmExports.init_particles === 'function') {
        wasmExports.init_particles(W, H, particleCount);
        requestAnimationFrame(drawParticles);
      }
      initGlobeWasm();
    })
    .catch(function () {
      initFallbackParticles();
    });


  /* ─── PARTICLE CANVAS ─── */
  var cvs = document.getElementById('particles');
  var ctx = cvs ? cvs.getContext('2d') : null;
  var W, H;
  var particleCount = 120;

  function resizeCanvas() {
    if (!cvs) return;
    W = cvs.width = window.innerWidth;
    H = cvs.height = window.innerHeight;
    if (wasmReady && typeof wasmExports.resize_particles === 'function') {
      wasmExports.resize_particles(W, H);
    }
  }

  if (cvs) {
    resizeCanvas();
    window.addEventListener('resize', resizeCanvas);
  }

  /* Fallback particles (no WASM) */
  var pts = [];
  function initFallbackParticles() {
    pts = [];
    for (var i = 0; i < particleCount; i++) {
      pts.push({
        x: Math.random() * W,
        y: Math.random() * H,
        r: Math.random() * 1.2 + 0.3,
        dx: (Math.random() - 0.5) * 0.15,
        dy: (Math.random() - 0.5) * 0.1 - 0.05,
        a: Math.random() * 0.4 + 0.05,
        gold: Math.random() < 0.2
      });
    }
    requestAnimationFrame(drawFallback);
  }

  function drawFallback() {
    if (wasmReady || !ctx) return;
    ctx.clearRect(0, 0, W, H);
    for (var i = 0; i < pts.length; i++) {
      var p = pts[i];
      p.x += p.dx;
      p.y += p.dy;
      if (p.x < 0) p.x = W;
      if (p.x > W) p.x = 0;
      if (p.y < 0) p.y = H;
      if (p.y > H) p.y = 0;
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
      ctx.fillStyle = p.gold
        ? 'rgba(201,168,76,' + p.a + ')'
        : 'rgba(255,255,255,' + (p.a * 0.35) + ')';
      ctx.fill();
    }
    requestAnimationFrame(drawFallback);
  }

  /* WASM particles */
  var lastPT = 0;
  function drawParticles(ts) {
    if (!wasmReady || !ctx) return;
    if (!lastPT) lastPT = ts;
    var dt = Math.min(ts - lastPT, 50);
    lastPT = ts;
    var ptr = wasmExports.tick_particles(dt);
    var n = wasmExports.particle_count();
    var buf = new Float32Array(wasmMem.buffer, ptr, n * 5);
    ctx.clearRect(0, 0, W, H);
    for (var i = 0; i < n; i++) {
      var o = i * 5;
      var x = buf[o], y = buf[o + 1], r = buf[o + 2], a = buf[o + 3], gold = buf[o + 4];
      ctx.beginPath();
      ctx.arc(x, y, r, 0, 6.2832);
      ctx.fillStyle = gold > 0.5
        ? 'rgba(201,168,76,' + a + ')'
        : 'rgba(255,255,255,' + (a * 0.35) + ')';
      ctx.fill();
    }
    requestAnimationFrame(drawParticles);
  }


  /* ─── NAVIGATION ─── */
  var nav = document.getElementById('topnav');
  var bttBtn = document.getElementById('btt');

  window.addEventListener('scroll', function () {
    nav.classList.toggle('scrolled', scrollY > 40);
    bttBtn.classList.toggle('show', scrollY > 300);
  }, { passive: true });

  bttBtn.addEventListener('click', function () {
    scrollTo({ top: 0, behavior: 'smooth' });
  });


  /* ─── MOBILE DRAWER ─── */
  var ham = document.getElementById('ham');
  var dr = document.getElementById('drawer');
  var dbg = document.getElementById('dbg');

  function closeDr() {
    dr.classList.remove('open');
    ham.classList.remove('on');
    ham.setAttribute('aria-expanded', 'false');
    document.body.classList.remove('locked');
  }

  ham.addEventListener('click', function () {
    var isOpen = dr.classList.toggle('open');
    ham.classList.toggle('on', isOpen);
    ham.setAttribute('aria-expanded', String(isOpen));
    document.body.classList.toggle('locked', isOpen);
  });

  dbg.addEventListener('click', closeDr);
  dr.querySelectorAll('.dl').forEach(function (a) {
    a.addEventListener('click', closeDr);
  });


  /* ─── GSAP SCROLL REVEAL ─── */
  function initGSAP() {
    if (typeof gsap === 'undefined' || typeof ScrollTrigger === 'undefined') {
      setTimeout(initGSAP, 100);
      return;
    }

    gsap.registerPlugin(ScrollTrigger);

    // Hero stagger
    gsap.to('.hero-inner .rv', {
      opacity: 1, y: 0, duration: 1, stagger: 0.15,
      ease: 'power3.out', delay: 0.3
    });

    // Globe section stagger
    gsap.to('.globe-sec .rv', {
      scrollTrigger: { trigger: '.globe-sec', start: 'top 85%' },
      opacity: 1, y: 0, duration: 0.8, stagger: 0.1, ease: 'power2.out'
    });

    // General section reveals
    document.querySelectorAll('.sec, .node-sec, .gib-sec, footer').forEach(function (sec) {
      var els = sec.querySelectorAll('.rv');
      if (els.length) {
        gsap.to(els, {
          scrollTrigger: { trigger: sec, start: 'top 85%', toggleActions: 'play none none none' },
          opacity: 1, y: 0, duration: 0.7, stagger: 0.08, ease: 'power2.out'
        });
      }
    });

    // Counter animation for globe stats
    var counted = false;
    ScrollTrigger.create({
      trigger: '.globe-sec', start: 'top 70%',
      onEnter: function () {
        if (counted) return;
        counted = true;
        document.querySelectorAll('.gd-n[data-count]').forEach(function (el) {
          var target = parseInt(el.dataset.count);
          var prefix = el.dataset.prefix || '';
          var suffix = el.dataset.suffix || '+';
          var dur = 1500, start = Date.now();
          function tick() {
            var prog = Math.min((Date.now() - start) / dur, 1);
            var ease = 1 - Math.pow(1 - prog, 3);
            var val = Math.round(target * ease);
            el.textContent = prefix + val.toLocaleString() + suffix;
            if (prog < 1) requestAnimationFrame(tick);
          }
          tick();
        });
      }
    });

    // GIB section parallax glow
    ScrollTrigger.create({
      trigger: '.gib-sec', start: 'top bottom', end: 'bottom top',
      onUpdate: function (self) {
        var glow = document.querySelector('.gib-sec::before');
        if (glow) glow.style.opacity = 0.5 + self.progress * 0.5;
      }
    });

    // GIB connector animation
    ScrollTrigger.create({
      trigger: '.gib-visual', start: 'top 80%',
      onEnter: function () {
        document.querySelectorAll('.gib-connector').forEach(function (c, i) {
          c.style.transition = 'opacity .6s ease ' + (i * 0.3) + 's';
          c.style.opacity = '1';
        });
      }
    });
  }

  initGSAP();


  /* ─── HERO TITLE ROTATION ─── */
  (function () {
    var titles = document.querySelectorAll('#hero-title-rotate .title-slogan');
    if (!titles.length) return;
    var idx = 0;
    setInterval(function () {
      titles[idx].classList.remove('active');
      idx = (idx + 1) % titles.length;
      titles[idx].classList.add('active');
    }, 4000);
  })();


  /* ─── GLOBE CAPITAL CITY LIGHTS (WASM) ─── */
  var gcv = document.getElementById('globeArcs');
  var gctx = gcv ? gcv.getContext('2d') : null;
  var gW, gH, gCX, gCY, gRadius;

  var capitals = [
    { name: 'Washington', lng: -77, lat: 38.9 },
    { name: 'New York', lng: -74, lat: 40.7 },
    { name: 'London', lng: -0.1, lat: 51.5 },
    { name: 'Paris', lng: 2.3, lat: 48.9 },
    { name: 'Berlin', lng: 13.4, lat: 52.5 },
    { name: 'Moscow', lng: 37.6, lat: 55.8 },
    { name: 'Dubai', lng: 55.3, lat: 25.3 },
    { name: 'Mumbai', lng: 72.9, lat: 19.1 },
    { name: 'Beijing', lng: 116.4, lat: 39.9 },
    { name: 'Shanghai', lng: 121.5, lat: 31.2 },
    { name: 'Tokyo', lng: 139.7, lat: 35.7 },
    { name: 'Singapore', lng: 103.8, lat: 1.3 },
    { name: 'Sydney', lng: 151.2, lat: -33.9 },
    { name: 'Sao Paulo', lng: -46.6, lat: -23.5 },
    { name: 'Lagos', lng: 3.4, lat: 6.5 },
    { name: 'Cairo', lng: 31.2, lat: 30.0 },
    { name: 'Seoul', lng: 127.0, lat: 37.6 },
    { name: 'Bangkok', lng: 100.5, lat: 13.8 },
    { name: 'Jakarta', lng: 106.8, lat: -6.2 },
    { name: 'Toronto', lng: -79.4, lat: 43.7 },
    { name: 'Mexico City', lng: -99.1, lat: 19.4 },
    { name: 'Riyadh', lng: 46.7, lat: 24.6 },
    { name: 'Johannesburg', lng: 28.0, lat: -26.2 },
    { name: 'HongKong', lng: 114.2, lat: 22.3 },
    { name: 'Zurich', lng: 8.5, lat: 47.4 }
  ];

  function resizeGlobe() {
    if (!gcv) return;
    var wrap = gcv.parentElement;
    gW = gcv.width = wrap.offsetWidth;
    gH = gcv.height = wrap.offsetHeight;
    gCX = gW / 2;
    gCY = gH / 2;
    gRadius = Math.min(gW, gH) / 2 * 0.88;
  }

  if (gcv) {
    resizeGlobe();
    window.addEventListener('resize', resizeGlobe);
  }

  function initGlobeWasm() {
    if (!gcv || !wasmReady || typeof wasmExports.init_globe !== 'function') return;
    wasmExports.init_globe(capitals.length);
    for (var i = 0; i < capitals.length; i++) {
      wasmExports.set_city(i, capitals[i].lng, capitals[i].lat);
    }
    requestAnimationFrame(drawGlobeWasm);
  }

  var lastGT = 0;
  function drawGlobeWasm(ts) {
    if (!wasmReady || !gcv) return;
    if (!lastGT) lastGT = ts;
    var dt = Math.min(ts - lastGT, 50);
    lastGT = ts;
    var rotOff = (ts / 1000) * (360 / 90);
    var ptr = wasmExports.tick_globe(rotOff, dt, gCX, gCY, gRadius);
    var n = wasmExports.city_count();
    var buf = new Float32Array(wasmMem.buffer, ptr, n * 6);
    gctx.clearRect(0, 0, gW, gH);
    for (var i = 0; i < n; i++) {
      var o = i * 6;
      if (buf[o + 5] < 0.5) continue;
      var x = buf[o], y = buf[o + 1], dotR = buf[o + 2], baseA = buf[o + 3], pulseA = buf[o + 4];
      gctx.beginPath();
      gctx.arc(x, y, dotR, 0, 6.2832);
      gctx.fillStyle = 'rgba(201,168,76,' + baseA + ')';
      gctx.fill();
      if (pulseA > 0.05) {
        gctx.beginPath();
        gctx.arc(x, y, dotR + 3 * pulseA, 0, 6.2832);
        gctx.fillStyle = 'rgba(245,230,184,' + (pulseA * 0.6) + ')';
        gctx.fill();
        gctx.beginPath();
        gctx.arc(x, y, dotR + 8 * pulseA, 0, 6.2832);
        gctx.fillStyle = 'rgba(201,168,76,' + (pulseA * 0.15) + ')';
        gctx.fill();
      }
    }
    requestAnimationFrame(drawGlobeWasm);
  }


  /* ─── CITY POPUP SEQUENCE ─── */
  (function () {
    var popup = document.getElementById('cityPopup');
    var nameEl = document.getElementById('cpName');
    var countEl = document.getElementById('cpCount');
    var msgEl = document.getElementById('cityMsg');
    var heroEl = document.querySelector('.globe-hero');
    if (!popup || !heroEl) return;

    var GLOBE_OFFSET = -30;
    var cities = [
      { name: '北京 Beijing', lng: 116.4, lat: 39.9, count: 98457 },
      { name: '成都 Chengdu', lng: 104.1, lat: 30.6, count: 76234 },
      { name: '西安 Xi\'an', lng: 108.9, lat: 34.3, count: 62891 },
      { name: '东京 Tokyo', lng: 139.7, lat: 35.7, count: 84762 },
      { name: '首尔 Seoul', lng: 127.0, lat: 37.6, count: 71837 },
      { name: '曼谷 Bangkok', lng: 100.5, lat: 13.8, count: 52345 },
      { name: '河内 Hanoi', lng: 105.8, lat: 21.0, count: 43562 },
      { name: '孟买 Mumbai', lng: 72.9, lat: 19.1, count: 67891 },
      { name: '德里 Delhi', lng: 77.2, lat: 28.6, count: 88234 },
      { name: '迪拜 Dubai', lng: 55.3, lat: 25.3, count: 31763 },
      { name: '伊斯坦布尔 Istanbul', lng: 28.9, lat: 41.0, count: 46789 },
      { name: '开罗 Cairo', lng: 31.2, lat: 30.1, count: 39456 },
      { name: '柏林 Berlin', lng: 13.4, lat: 52.5, count: 54321 },
      { name: '巴黎 Paris', lng: 2.3, lat: 48.9, count: 63789 },
      { name: '马德里 Madrid', lng: -3.7, lat: 40.4, count: 41237 },
      { name: '伦敦 London', lng: -0.1, lat: 51.5, count: 58912 },
      { name: '纽约 New York', lng: -74.0, lat: 40.7, count: 77890 },
      { name: '芝加哥 Chicago', lng: -87.6, lat: 41.8, count: 53647 },
      { name: '多伦多 Toronto', lng: -79.4, lat: 43.7, count: 48231 },
      { name: '圣保罗 Sao Paulo', lng: -46.6, lat: -23.5, count: 62345 },
      { name: '悉尼 Sydney', lng: 151.2, lat: -33.9, count: 35678 },
      { name: '约翰内斯堡 Johannesburg', lng: 28.0, lat: -26.2, count: 28934 }
    ];

    var idx = 0, phase = 'idle', phaseTimer = 0;
    var started = false;

    function project(lng, lat, rotOff) {
      var w = heroEl.offsetWidth;
      var cX = w / 2, cY = w / 2, radius = w / 2 * 0.9;
      var effLng = (lng + GLOBE_OFFSET - rotOff) * Math.PI / 180;
      var latRad = lat * Math.PI / 180;
      var cosLat = Math.cos(latRad);
      var x = cosLat * Math.sin(effLng);
      var z = cosLat * Math.cos(effLng);
      var y = -Math.sin(latRad);
      if (z < 0.15) return null;
      return { x: cX + x * radius * 0.88, y: cY + y * radius * 0.88, z: z };
    }

    function countUp(target, dur) {
      var start = Date.now();
      (function tick() {
        var p = Math.min((Date.now() - start) / dur, 1);
        var ease = 1 - Math.pow(1 - p, 3);
        countEl.innerHTML = Math.round(target * ease).toLocaleString() + '<span>人</span>';
        if (p < 1) requestAnimationFrame(tick);
      })();
    }

    function resetPopup() {
      popup.style.display = 'none';
      popup.style.opacity = '0';
      var line = popup.querySelector('.cp-line');
      if (line) line.classList.remove('grow');
      var card = popup.querySelector('.cp-card');
      if (card) { card.classList.remove('show'); card.style.opacity = '0'; }
    }

    var lastTs = 0;
    function animate(ts) {
      if (!lastTs) lastTs = ts;
      var dt = ts - lastTs;
      lastTs = ts;
      var rotOff = (ts / 1000) * (360 / 90);

      if (phase === 'show-dot' || phase === 'grow-line' || phase === 'show-card' || phase === 'hold') {
        var c = cities[idx];
        var p = project(c.lng, c.lat, rotOff);
        if (p) {
          popup.style.display = 'block';
          popup.style.left = p.x + 'px';
          popup.style.top = p.y + 'px';
          popup.style.opacity = String(Math.min(1, 0.4 + p.z * 0.7));
        } else {
          popup.style.opacity = '0';
        }
      }

      phaseTimer -= dt;
      if (phaseTimer <= 0) {
        switch (phase) {
          case 'idle':
            var tries = 0, found = false;
            while (tries < cities.length) {
              var ro = (Date.now() / 1000) * (360 / 90);
              var pr = project(cities[idx].lng, cities[idx].lat, ro);
              if (pr && pr.z > 0.25) { found = true; break; }
              idx = (idx + 1) % cities.length;
              tries++;
            }
            if (!found) { phaseTimer = 600; break; }
            nameEl.textContent = cities[idx].name;
            countEl.innerHTML = '0<span>人</span>';
            resetPopup();
            popup.style.display = 'block';
            phase = 'show-dot';
            phaseTimer = 350;
            break;

          case 'show-dot':
            popup.querySelector('.cp-line').classList.add('grow');
            phase = 'grow-line';
            phaseTimer = 450;
            break;

          case 'grow-line':
            var card = popup.querySelector('.cp-card');
            card.style.opacity = '1';
            card.classList.add('show');
            countUp(cities[idx].count, 900);
            phase = 'show-card';
            phaseTimer = 1000;
            break;

          case 'show-card':
            phase = 'hold';
            phaseTimer = 1400;
            break;

          case 'hold':
            popup.style.transition = 'opacity .4s ease';
            popup.style.opacity = '0';
            phase = 'fade';
            phaseTimer = 450;
            break;

          case 'fade':
            resetPopup();
            idx = (idx + 1) % cities.length;
            if (idx % 5 === 0) { phase = 'msg-show'; phaseTimer = 200; }
            else { phase = 'idle'; phaseTimer = 300; }
            break;

          case 'msg-show':
            msgEl.classList.add('show');
            phase = 'msg-hold';
            phaseTimer = 2800;
            break;

          case 'msg-hold':
            msgEl.classList.remove('show');
            phase = 'msg-fade';
            phaseTimer = 900;
            break;

          case 'msg-fade':
            phase = 'idle';
            phaseTimer = 400;
            break;
        }
      }

      requestAnimationFrame(animate);
    }

    if (typeof ScrollTrigger !== 'undefined') {
      waitForScrollTrigger();
    } else {
      setTimeout(waitForScrollTrigger, 200);
    }

    function waitForScrollTrigger() {
      if (typeof gsap === 'undefined' || typeof ScrollTrigger === 'undefined') {
        setTimeout(waitForScrollTrigger, 100);
        return;
      }
      ScrollTrigger.create({
        trigger: '.globe-sec', start: 'top 70%',
        onEnter: function () {
          if (!started) {
            started = true;
            phase = 'idle';
            phaseTimer = 0;
            requestAnimationFrame(animate);
          }
        }
      });
    }
  })();


  /* ─── GIB GOLD PRICE SHIMMER ─── */
  (function () {
    var el = document.getElementById('gold-price');
    if (!el) return;
    var base = 2350;
    setInterval(function () {
      var fluctuation = (Math.random() - 0.5) * 8;
      var price = (base + fluctuation).toFixed(2);
      el.textContent = '$' + Number(price).toLocaleString('en-US', { minimumFractionDigits: 2 }) + '/oz';
    }, 3000);
  })();


  /* ─── SMOOTH ANCHOR SCROLL ─── */
  document.querySelectorAll('a[href^="#"]').forEach(function (a) {
    a.addEventListener('click', function (e) {
      var id = this.getAttribute('href');
      if (id === '#') return;
      var target = document.querySelector(id);
      if (target) {
        e.preventDefault();
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    });
  });


  /* ─── CMS INTEGRATION ─── */
  function $id(id) { return document.getElementById(id); }
  function $q(sel) { return document.querySelector(sel); }
  function $qa(sel) { return document.querySelectorAll(sel); }
  function esc(s) { var d = document.createElement('div'); d.textContent = s; return d.innerHTML; }
  function ea(s) { return s.replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;'); }

  fetch(location.origin + '/api/admin/public/website/content')
    .then(function (r) { return r.json(); })
    .then(function (res) {
      if (res.code !== 0 || !res.data) return;
      var d = res.data;

      // Nav
      if (d.website_nav) {
        var n = d.website_nav;
        if (n.brandName) {
          var logo = $q('#topnav .nav-logo');
          if (logo) logo.innerHTML = '<span class="nav-logo-text">' + esc(n.brandName) + '</span><small>AGX DIGITAL CAPITAL GROUP</small>';
        }
        if (n.menuItems && n.menuItems.length) {
          var nl = $q('#topnav .nav-links');
          var dp = $q('.drawer-panel');
          if (nl) nl.innerHTML = n.menuItems.map(function (m) {
            return '<a href="' + ea(m.anchor || '#') + '">' + esc(m.text) + '</a>';
          }).join('') + '<a href="' + ea(n.downloadLink || 'https://jkda2.com') + '" class="nav-cta" target="_blank" rel="noopener">' + esc(n.downloadText || '下载APP') + '</a>';
          if (dp) dp.innerHTML = n.menuItems.map(function (m) {
            return '<a href="' + ea(m.anchor || '#') + '" class="dl">' + esc(m.text) + '</a>';
          }).join('') + '<a href="' + ea(n.downloadLink || 'https://jkda2.com') + '" class="btn-dl" target="_blank" rel="noopener">' + esc(n.downloadText || '进入平台') + '</a>';
        }
      }

      // Hero
      if (d.website_hero) {
        var h = d.website_hero;
        var hd = $id('cms-hero-desc');
        if (h.description && hd) hd.textContent = h.description;
      }

      // Stats
      if (d.website_stats && d.website_stats.length) {
        var gds = document.querySelectorAll('#globeData .gd');
        d.website_stats.forEach(function (s, i) {
          if (gds[i]) {
            var lbl = gds[i].querySelector('.gd-l');
            if (lbl && s.label) lbl.textContent = s.label;
          }
        });
      }

      // About
      if (d.website_about) {
        var a = d.website_about;
        var abSec = $id('about');
        if (abSec) {
          if (a.tag) { var at = abSec.querySelector('.sec-tag'); if (at) at.textContent = a.tag; }
          if (a.title) { var atl = abSec.querySelector('.sec-title'); if (atl) atl.textContent = a.title; }
          if (a.intro1 || a.intro2 || a.intro3) {
            var abt = abSec.querySelector('.about-text');
            if (abt) {
              var ps = abt.querySelectorAll('p');
              if (a.intro1 && ps[0]) ps[0].textContent = a.intro1;
              if (a.intro2 && ps[1]) ps[1].textContent = a.intro2;
              if (a.intro3 && ps[2]) ps[2].textContent = a.intro3;
            }
          }
        }
      }

      // Business
      if (d.website_business) {
        var b = d.website_business;
        var bSec = $id('business');
        if (bSec) {
          if (b.tag) { var bt = bSec.querySelector('.sec-tag'); if (bt) bt.textContent = b.tag; }
          if (b.title) { var btl = bSec.querySelector('.sec-title'); if (btl) btl.textContent = b.title; }
          if (b.desc) { var bd = bSec.querySelector('.sec-desc'); if (bd) bd.textContent = b.desc; }
          if (b.items && b.items.length) {
            var pg = bSec.querySelector('.prod-grid');
            if (pg) pg.innerHTML = b.items.map(function (i) {
              return '<div class="p-card rv"><div class="p-icon">' + (i.icon || '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/></svg>') + '</div><h4>' + esc(i.title) + '</h4><p>' + esc(i.desc || '') + '</p></div>';
            }).join('');
          }
        }
      }

      // Team
      if (d.website_team && d.website_team.length) {
        var te = $id('cms-team');
        if (te) te.innerHTML = d.website_team.map(function (m) {
          return '<div class="t-card rv"><div class="t-av"><img src="' + ea(m.avatar || '') + '" alt="' + esc(m.name) + '" loading="lazy" onerror="this.onerror=null;this.src=\'data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 viewBox=%270 0 100 100%27%3E%3Crect width=%27100%27 height=%27100%27 fill=%27%23161a22%27/%3E%3Ccircle cx=%2750%27 cy=%2736%27 r=%2716%27 fill=%27rgba(201,168,76,0.3)%27/%3E%3Cellipse cx=%2750%27 cy=%2778%27 rx=%2726%27 ry=%2718%27 fill=%27rgba(201,168,76,0.2)%27/%3E%3C/svg%3E\'"></div><div class="t-tag">' + esc(m.roleTag || '') + '</div><h4>' + esc(m.name) + '</h4><div class="t-role">' + esc(m.title || '') + '</div><div class="t-bio">' + esc(m.bio || '') + '</div></div>';
        }).join('');
      }

      // Security
      if (d.website_security) {
        var s = d.website_security;
        var sSec = $id('security');
        if (sSec) {
          if (s.tag) { var st = sSec.querySelector('.sec-tag'); if (st) st.textContent = s.tag; }
          if (s.title) { var stl = sSec.querySelector('.sec-title'); if (stl) stl.textContent = s.title; }
          if (s.desc) { var sd = sSec.querySelector('.sec-desc'); if (sd) sd.textContent = s.desc; }
          if (s.items && s.items.length) {
            var sg = sSec.querySelector('.security-grid');
            if (sg) sg.innerHTML = s.items.map(function (i) {
              return '<div class="s-card rv"><div class="s-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg></div><div><h4>' + esc(i.title) + '</h4><p>' + esc(i.desc || '') + '</p></div></div>';
            }).join('');
          }
          if (s.audits && s.audits.length) {
            var ar = sSec.querySelector('.audit-row');
            if (ar) ar.innerHTML = s.audits.map(function (a) {
              return '<div class="audit"><strong>' + esc(a.name) + '</strong><span>' + esc(a.desc || '') + '</span></div>';
            }).join('');
          }
        }
      }

      // Licenses
      if (d.website_licenses && d.website_licenses.length) {
        var le = $id('cms-licenses');
        if (le) le.innerHTML = d.website_licenses.map(function (l) {
          return '<div class="lic rv"><img src="' + ea(l.image || '') + '" alt="' + esc(l.name) + '" loading="lazy"><h5>' + esc(l.name) + '</h5><p>' + esc(l.description || '') + '</p></div>';
        }).join('');
      }

      // Footer
      if (d.website_footer) {
        var f = d.website_footer;
        if (f.brandDesc || f.desc) { $qa('.ft-desc').forEach(function (el) { el.textContent = f.brandDesc || f.desc; }); }
        if (f.brandName) { $qa('.ft-logo').forEach(function (el) { el.textContent = f.brandName; }); }
      }

      // Force-show CMS-rendered elements
      setTimeout(function () {
        document.querySelectorAll('#cms-team .rv, #cms-licenses .rv, #business .p-card, #security .s-card, #about .rv').forEach(function (el) {
          el.style.opacity = '1';
          el.style.transform = 'none';
        });
      }, 100);
    })
    .catch(function (e) { console.warn('CMS fetch error:', e); });

})();
