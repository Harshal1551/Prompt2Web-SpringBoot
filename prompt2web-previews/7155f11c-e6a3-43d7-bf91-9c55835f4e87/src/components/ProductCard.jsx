import { Link } from 'react-router-dom';
import { Star, ShoppingBag } from 'lucide-react';
import { formatPrice } from '../data/products.js';
import { useCart } from '../context/CartContext.jsx';

export default function ProductCard({ product }) {
  const { addToCart } = useCart();
  const discount = product.originalPrice
    ? Math.round((1 - product.price / product.originalPrice) * 100)
    : 0;

  return (
    <article className="group flex flex-col overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm transition hover:-translate-y-1 hover:shadow-soft-lg">
      <Link to={`/product/${product.id}`} className="relative block overflow-hidden">
        <div
          className={`flex h-52 items-center justify-center bg-gradient-to-br ${product.gradient} transition duration-300 group-hover:scale-105 sm:h-60`}
        >
          <span className="text-6xl drop-shadow-lg sm:text-7xl" aria-hidden="true">
            {product.emoji}
          </span>
        </div>
        <div className="absolute left-3 top-3 flex gap-2">
          {product.badge && (
            <span className="rounded-full bg-white/90 px-2.5 py-1 text-xs font-semibold text-slate-800 backdrop-blur">
              {product.badge}
            </span>
          )}
          {discount > 0 && (
            <span className="rounded-full bg-emerald-500 px-2.5 py-1 text-xs font-semibold text-white">
              -{discount}%
            </span>
          )}
        </div>
      </Link>

      <div className="flex flex-1 flex-col p-4 sm:p-5">
        <p className="text-xs font-semibold uppercase tracking-wider text-brand-600">{product.category}</p>
        <Link
          to={`/product/${product.id}`}
          className="mt-1.5 line-clamp-1 font-semibold text-slate-900 transition hover:text-brand-700"
        >
          {product.name}
        </Link>
        <div className="mt-1.5 flex items-center gap-1 text-xs text-slate-500">
          <Star className="h-3.5 w-3.5 fill-amber-400 text-amber-400" />
          <span className="font-medium text-slate-700">{product.rating.toFixed(1)}</span>
          <span>·</span>
          <span>{product.reviews} reviews</span>
        </div>

        <div className="mt-auto flex items-end justify-between gap-3 pt-4">
          <div>
            <p className="text-lg font-bold text-slate-900">{formatPrice(product.price)}</p>
            {product.originalPrice && (
              <p className="text-xs text-slate-400 line-through">{formatPrice(product.originalPrice)}</p>
            )}
          </div>
          <button
            type="button"
            onClick={() => addToCart(product)}
            className="inline-flex items-center gap-2 rounded-xl bg-slate-900 px-3.5 py-2.5 text-sm font-semibold text-white transition hover:bg-brand-600 active:scale-95"
          >
            <ShoppingBag className="h-4 w-4" />
            Add
          </button>
        </div>
      </div>
    </article>
  );
}
