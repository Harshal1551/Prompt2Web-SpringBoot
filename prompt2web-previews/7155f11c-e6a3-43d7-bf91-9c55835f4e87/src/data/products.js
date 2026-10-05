export const CATEGORIES = ['All', 'Audio', 'Wearables', 'Home', 'Office'];

export const products = [
  {
    id: 'aurora-headphones',
    name: 'Aurora Wireless Headphones',
    tagline: 'Adaptive noise cancelling, studio sound',
    category: 'Audio',
    price: 199,
    originalPrice: 249,
    rating: 4.8,
    reviews: 214,
    emoji: '🎧',
    gradient: 'from-indigo-500 via-violet-500 to-fuchsia-500',
    badge: 'Best Seller',
    stock: 12,
    description:
      'Immersive over-ear headphones with adaptive noise cancelling, a 40-hour battery and memory-foam cushions tuned for marathon listening sessions.',
    features: [
      'Adaptive active noise cancelling',
      '40-hour battery, 5-minute quick charge',
      'Memory-foam ear cushions',
      'Bluetooth 5.3 with multipoint pairing'
    ]
  },
  {
    id: 'pulse-smartwatch',
    name: 'Pulse Smart Watch',
    tagline: 'Your health, on your wrist',
    category: 'Wearables',
    price: 129,
    originalPrice: null,
    rating: 4.6,
    reviews: 187,
    emoji: '⌚',
    gradient: 'from-sky-500 to-cyan-400',
    badge: null,
    stock: 25,
    description:
      'A sleek smart watch with a always-on AMOLED display, heart-rate, sleep and SpO2 tracking, plus 7-day battery life in a featherlight case.',
    features: [
      '1.4-inch always-on AMOLED display',
      'Heart rate, sleep & SpO2 tracking',
      '7-day battery life',
      '5 ATM water resistance'
    ]
  },
  {
    id: 'crisp-earbuds',
    name: 'Crisp Earbuds Pro',
    tagline: 'Tiny buds, huge sound',
    category: 'Audio',
    price: 89,
    originalPrice: 129,
    rating: 4.7,
    reviews: 320,
    emoji: '🎵',
    gradient: 'from-rose-500 to-pink-500',
    badge: '-31%',
    stock: 40,
    description:
      'True wireless earbuds with hybrid ANC, wireless charging and an IPX5 rating that shrugs off rain, sweat and the occasional splash.',
    features: [
      'Hybrid active noise cancelling',
      'Wireless charging case',
      'IPX5 sweat & water resistance',
      '6 hours + 24 hours with case'
    ]
  },
  {
    id: 'lumen-lamp',
    name: 'Lumen Desk Lamp',
    tagline: 'Light that adapts to you',
    category: 'Home',
    price: 59,
    originalPrice: null,
    rating: 4.5,
    reviews: 98,
    emoji: '💡',
    gradient: 'from-amber-400 to-orange-500',
    badge: null,
    stock: 18,
    description:
      'An architect-style desk lamp with tunable white light, app control and a discreet wireless phone-charging pad built into the base.',
    features: [
      '2700K-6500K tunable white light',
      'Built-in wireless charger base',
      'Memory functions & app dimming',
      'Brushed aluminium body'
    ]
  },
  {
    id: 'zen-ganery',
    name: 'Zen Planter & Grower',
    tagline: 'Your garden, automated',
    category: 'Home',
    price: 119,
    originalPrice: 149,
    rating: 4.4,
    reviews: 76,
    emoji: '🌿',
    gradient: 'from-emerald-500 to-teal-400',
    badge: 'New',
    stock: 9,
    description:
      'A smart hydroponic planter that waters, lights and monitors herbs and greens for you, right on your kitchen counter.',
    features: [
      'Auto watering & fertilising',
      'Full-spectrum grow light',
      'Sensors alert you before issues',
      'Silent pump, no-mess reservoir'
    ]
  },
  {
    id: 'volt-powerbank',
    name: 'Volt Power Bank 20K',
    tagline: 'Charge everything, anywhere',
    category: 'Tech',
    price: 49,
    originalPrice: null,
    rating: 4.3,
    reviews: 412,
    emoji: '🔋',
    gradient: 'from-slate-700 to-slate-500',
    badge: null,
    stock: 60,
    description:
      'A compact 20,000 mAh power bank with 65 W fast charging, dual USB-C ports and an LED display showing exact charge remaining.',
    features: [
      '65 W USB-C fast charging',
      '20,000 mAh capacity',
      'LED charge display',
      'Charges 3 devices at once'
    ]
  },
  {
    id: 'tactile-keyboard',
    name: 'Tactile Mechanical Keyboard',
    tagline: 'Typing you will feel',
    category: 'Office',
    price: 149,
    originalPrice: 179,
    rating: 4.9,
    reviews: 154,
    emoji: '⌨️',
    gradient: 'from-zinc-800 to-zinc-600',
    badge: 'Top Rated',
    stock: 14,
    description:
      'A gasket-mounted mechanical keyboard with hot-swappable switches, tri-mode connectivity and south-facing RGB that stays out of your way.',
    features: [
      'Hot-swappable switches',
      'Gasket mount for soft typing',
      'Bluetooth, 2.4 GHz & USB-C',
      'South-facing RGB'
    ]
  },
  {
    id: 'drift-mouse',
    name: 'Drift Ergonomic Mouse',
    tagline: 'Hours of comfort',
    category: 'Office',
    price: 79,
    originalPrice: null,
    rating: 4.6,
    reviews: 203,
    emoji: '🖱️',
    gradient: 'from-cyan-500 to-blue-500',
    badge: null,
    stock: 30,
    description:
      'A sculpted ergonomic mouse with a 26K DPI optical sensor, six programmable buttons and a silent click mechanism.',
    features: [
      '26,000 DPI optical sensor',
      '6 programmable buttons',
      'Silent click mechanism',
      'USB-C & 2.4 GHz wireless'
    ]
  },
  {
    id: 'vortex-speaker',
    name: 'Vortex 360 Speaker',
    tagline: 'Room-filling, 360° sound',
    category: 'Audio',
    price: 99,
    originalPrice: 129,
    rating: 4.5,
    reviews: 167,
    emoji: '🔊',
    gradient: 'from-violet-600 to-purple-500',
    badge: 'Sale',
    stock: 22,
    description:
      'A 360° smart speaker with adaptive EQ, party-pair stereo mode and 20 hours of battery for sound that fills the room.',
    features: [
      '360° omnidirectional sound',
      'Adaptive room EQ',
      '20-hour battery',
      'IP67 dust & waterproof'
    ]
  },
  {
    id: 'clip-band',
    name: 'Clip Fitness Band',
    tagline: 'Lightweight activity tracking',
    category: 'Wearables',
    price: 59,
    originalPrice: null,
    rating: 4.2,
    reviews: 88,
    emoji: '📿',
    gradient: 'from-lime-500 to-green-500',
    badge: null,
    stock: 45,
    description:
      'A featherlight clip-on tracker with 14-day battery, sleep scoring, workout auto-detect and haptic reminders to move.',
    features: [
      '14-day battery life',
      'Sleep scoring & readiness',
      'Auto workout detection',
      '50 m water resistance'
    ]
  },
  {
    id: 'studio-cam',
    name: 'Studio 4K Webcam',
    tagline: 'Look sharp on every call',
    category: 'Office',
    price: 89,
    originalPrice: 109,
    rating: 4.4,
    reviews: 131,
    emoji: '📷',
    gradient: 'from-gray-700 to-slate-500',
    badge: null,
    stock: 27,
    description:
      'A 4K webcam with AI framing, dual mics with noise suppression and a privacy shutter that keeps your meetings sharp and private.',
    features: [
      '4K @ 30fps sensor',
      'AI auto framing',
      'Dual mics with noise suppression',
      'Built-in privacy shutter'
    ]
  },
  {
    id: 'brew-machine',
    name: 'Brew Mini Machine',
    tagline: 'Cafe coffee, zero fuss',
    category: 'Home',
    price: 139,
    originalPrice: null,
    rating: 4.7,
    reviews: 240,
    emoji: '☕',
    gradient: 'from-amber-600 to-yellow-500',
    badge: 'Popular',
    stock: 11,
    description:
      'A compact espresso machine with a 15-bar pump, built-in grinder and milk steamer that fits on any counter.',
    features: [
      '15-bar pump for rich crema',
      'Built-in conical burr grinder',
      'One-touch milk steamer',
      'Compact counter footprint'
    ]
  }
];

export const formatPrice = (value) =>
  new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
