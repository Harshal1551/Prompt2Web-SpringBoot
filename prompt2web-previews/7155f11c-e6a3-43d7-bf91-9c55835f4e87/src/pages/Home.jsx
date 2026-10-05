import { Link, useNavigate } from 'react-router-dom';
import { Star, ArrowRight, Truck, ShieldCheck, RotateCcw, Headphones, Sparkles } from 'lucide-react';
import { products, CATEGORIES } from '../data/products.js';
import ProductCard from '../components/ProductCard.jsx';

const PERKS = [
  { icon: Truck, title: 'Free shipping', text: 'On all orders over $75' },
  { icon: ShieldCheck, title: '2-year warranty', text: 'Every product, no fine print' },
  { icon: RotateCcw, title: '30-day returns', text: 'Changed your mind? Easy.' },
  { icon: Headphones, title: '24/7 support', text: 'Real humans, always on' }
];

export default function Home() {
  const navigate = useNavigate();
  const featured = products.slice(0, 8);
  const topRated = [...products].sort((a, b) => b.rating - a.rating).slice(0, 4);

  return (
    <div className="animate-fade-in">
      <section className="relative overflow-hidden bg-slate-900 text-white">
        <div
          aria-hidden="true"
          className="absolute inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(99,102,241,0.35),transparent_55%),radial-gradient(ellipse_at_bottom_right,rgba(217,70,239,0.25),transparent_55%)]"
        />
        <div className="relative mx-auto grid max-w-7xl gap-12 px-4 py-16 sm:px-6 lg:grid-cols-2 lg:items-center lg:px-8 lg:py-24">
          <div className="animate-fade-up">
            <span className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3.5 py-1.5 text-xs font-semibold uppercase tracking-wider text-brand-200">
              <Sparkles className="h-3.5 w-3.5" />
              New season, new gear
            </span>
            <h1 className="mt-5 text-4xl font-bold leading-tight tracking-tight sm:text-5xl lg:text-6xl">
              Everything you need,{' '}
              <span className="bg-gradient-to-r from-brand-300 via-fuchsia-300 to-rose-300 bg-clip-text text-transparent">
                beautifully made
              </span>
            </h1>
            <p className="mt-5 max-w-lg text-base leading-7 text-slate-300 sm:text-lg">
              Discover a curated range of tech, audio and home essentials. Premium design, honest prices, delivery you can count on.
            </p>
            <div className="mt-8 flex flex-wrap gap-4">
              <button
                type="button"
                onClick={() => navigate('/shop')}
                className="inline-flex items-center gap-2 rounded-xl bg-brand-500 px-6 py-3.5 text-sm font-semibold text-white shadow-soft-lg transition hover:bg-brand-400"
              >
                Shop the collection
                <ArrowRight className="h-4 w-4" />
              </button>
              <button
                type="button"
                onClick={() => navigate('/shop?category=Audio')}
                className="inline-flex items-center gap-2 rounded-xl border border-white/20 bg-white/5 px-6 py-3.5 text-sm font-semibold text-white backdrop-blur transition hover:bg-white/10"
              >
                Explore audio
              </button>
            </div>
            <div className="mt-10 flex flex-wrap items-center gap-x-8 gap-y-4 text-sm text-slate-300">
              <div>
                <p className="text-2xl font-bold text-white">50k+</p>
                <p className="text-slate-400">Happy customers</p>
              </div>
              <div>
                <p className="text-2xl font-bold text-white">4.8/5</p>
                <p className="flex items-center gap-1 text-slate-400">
                  <Star className="h-3.5 w-3.5 fill-amber-400 text-amber-400" /> Average rating
                </p>
              </div>
              <div>
                <p className="text-2xl font-bold text-white">120+</p>
                <p className="text-slate-400">Premium products</p>
              </div>
            </div>
          </div>

          <div className="relative mx-auto w-full max-w-md animate-fade-up lg:max-w-none">
            <div className="grid grid-cols-2 gap-5">
              <div className="col-span-2 flex h-44 items-center justify-center rounded-3xl bg-gradient-to-br from-brand-500 to-fuchsia-500 shadow-soft-lg sm:h-52">
                <span className="text-7xl drop-shadow-lg sm:text-8xl" aria-hidden="true">
                  🎧
                </span>
              </div>
              <div className="flex h-44 items-center justify-center rounded-3xl bg-gradient-to-br from-sky-500 to-cyan-400 sm:h-48">
                <span className="text-6xl drop-shadow-lg sm:text-7xl" aria-hidden="true">
                  ⌚
                </span>
              </div>
              <div className="flex h-44 items-center justify-center rounded-3xl bg-gradient-to-br from-amber-400 to-orange-500 sm:h-48">
                <span className="text-6xl drop-shadow-lg sm:text-7xl" aria-hidden="true">
                  💡
                </span>
              </div>
            </div>
            <div className="absolute -left-4 bottom-10 hidden rounded-2xl bg-white p-4 shadow-xl sm:block">
              <div className="flex items-center gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-100 text-emerald-600">
                  <ShieldCheck className="h-5 w-5" />
                </div>
                <div>
                  <p className="text-sm font-bold text-slate-900">2-year warranty</p>
                  <p className="text-xs text-slate-500">Included with every order</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="grid gap-5 py-12 sm:grid-cols-2 lg:grid-cols-4">
          {PERKS.map(({ icon: Icon, title, text }) => (
            <div
              key={title}
              className="flex items-start gap-4 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
            >
              <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-brand-50 text-brand-600">
                <Icon className="h-5 w-5" />
              </div>
              <div>
                <p className="font-semibold text-slate-900">{title}</p>
                <p className="mt-0.5 text-sm text-slate-500">{text}</p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section id="shop" className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <h2 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">Shop by category</h2>
            <p className="mt-1 text-slate-500">Find exactly what you are looking for.</p>
          </div>
          <Link to="/shop" className="inline-flex items-center gap-1.5 text-sm font-semibold text-brand-600 hover:text-brand-700">
            View all products
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
        <div className="mt-6 flex gap-3 overflow-x-auto pb-2 no-scrollbar">
          {CATEGORIES.map((category, index) => (
            <Link
              key={category}
              to={index === 0 ? '/shop' : `/shop?category=${encodeURIComponent(category)}`}
              className="shrink-0 rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm font-medium text-slate-700 shadow-sm transition hover:border-brand-300 hover:text-brand-700"
            >
              {category}
            </Link>
          ))}
        </div>
        <div className="mt-8 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {featured.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </section>

      <section className="mx-auto mt-16 max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="rounded-3xl bg-slate-900 px-6 py-12 sm:px-10">
          <div className="flex flex-wrap items-end justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">Top rated this week</h2>
              <p className="mt-1 text-slate-400">Loved by our community, rated 4.4 and above.</p>
            </div>
            <Link to="/shop" className="inline-flex items-center gap-1.5 text-sm font-semibold text-brand-300 hover:text-brand-200">
              See the collection
              <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
          <div className="mt-8 grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
            {topRated.map((product) => (
              <Link
                key={product.id}
                to={`/product/${product.id}`}
                className="group rounded-2xl border border-white/10 bg-white/5 p-4 transition hover:bg-white/10"
              >
                <div
                  className={`flex h-32 items-center justify-center rounded-xl bg-gradient-to-br ${product.gradient} transition group-hover:scale-[1.02]`}
                >
                  <span className="text-5xl drop-shadow-lg" aria-hidden="true">
                    {product.emoji}
                  </span>
                </div>
                <div className="mt-4 flex items-center gap-1 text-xs text-slate-300">
                  <Star className="h-3.5 w-3.5 fill-amber-400 text-amber-400" />
                  <span className="font-semibold text-white">{product.rating.toFixed(1)}</span>
                  <span>· {product.reviews} reviews</span>
                </div>
                <p className="mt-1.5 line-clamp-1 font-semibold text-white group-hover:text-brand-300">
                  {product.name}
                </p>
              </Link>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
