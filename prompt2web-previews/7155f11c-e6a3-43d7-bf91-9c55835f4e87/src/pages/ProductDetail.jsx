import { useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import {
  Star,
  Check,
  ShoppingBag,
  Truck,
  ShieldCheck,
  RotateCcw,
  ArrowLeft,
  Minus,
  Plus
} from 'lucide-react';
import { products, formatPrice } from '../data/products.js';
import { useCart } from '../context/CartContext.jsx';
import ProductCard from '../components/ProductCard.jsx';

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const [quantity, setQuantity] = useState(1);

  const product = products.find((p) => p.id === id);

  if (!product) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-24 text-center sm:px-6">
        <div className="text-6xl">🔍</div>
        <h1 className="mt-6 text-2xl font-bold text-slate-900">Product not found</h1>
        <p className="mt-2 text-slate-500">The product you are looking for does not exist or was removed.</p>
        <Link
          to="/shop"
          className="mt-6 inline-flex items-center gap-2 rounded-xl bg-brand-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-brand-500"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to shop
        </Link>
      </div>
    );
  }

  const related = products
    .filter((p) => p.category === product.category && p.id !== product.id)
    .slice(0, 4);

  const discount = product.originalPrice
    ? Math.round((1 - product.price / product.originalPrice) * 100)
    : 0;

  const handleAddToCart = () => {
    addToCart(product, quantity);
  };

  const handleBuyNow = () => {
    addToCart(product, quantity);
    navigate('/checkout');
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
      <Link
        to="/shop"
        className="inline-flex items-center gap-1.5 text-sm font-medium text-slate-500 transition hover:text-slate-900"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to shop
      </Link>

      <div className="mt-6 grid gap-10 lg:grid-cols-2">
        <div className={`flex h-80 items-center justify-center rounded-3xl bg-gradient-to-br ${product.gradient} shadow-soft-lg sm:h-[28rem] lg:h-[32rem]`}>
          <span className="text-8xl drop-shadow-lg sm:text-9xl" aria-hidden="true">
            {product.emoji}
          </span>
        </div>

        <div className="flex flex-col">
          <div className="flex items-center gap-2">
            <span className="rounded-full bg-brand-50 px-3 py-1 text-xs font-semibold uppercase tracking-wider text-brand-700">
              {product.category}
            </span>
            {product.badge && (
              <span className="rounded-full bg-slate-900 px-3 py-1 text-xs font-semibold text-white">
                {product.badge}
              </span>
            )}
            {discount > 0 && (
              <span className="rounded-full bg-emerald-500 px-3 py-1 text-xs font-semibold text-white">
                -{discount}%
              </span>
            )}
          </div>

          <h1 className="mt-4 text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">{product.name}</h1>
          <p className="mt-2 text-lg text-slate-500">{product.tagline}</p>

          <div className="mt-4 flex items-center gap-2 text-sm">
            <div className="flex items-center gap-1">
              <Star className="h-4 w-4 fill-amber-400 text-amber-400" />
              <span className="font-semibold text-slate-900">{product.rating.toFixed(1)}</span>
            </div>
            <span className="text-slate-300">|</span>
            <a href="#reviews" className="text-brand-600 hover:underline">
              {product.reviews} reviews
            </a>
            <span className="text-slate-300">|</span>
            <span className="text-slate-500">SKU: {product.id.toUpperCase()}</span>
          </div>

          <div className="mt-6 flex items-baseline gap-3">
            <span className="text-4xl font-bold text-slate-900">{formatPrice(product.price)}</span>
            {product.originalPrice && (
              <span className="text-lg text-slate-400 line-through">{formatPrice(product.originalPrice)}</span>
            )}
          </div>

          <p className="mt-5 leading-7 text-slate-600">{product.description}</p>

          <ul className="mt-6 space-y-2.5">
            {product.features.map((feature) => (
              <li
                key={feature}
                className="flex items-center gap-2.5 text-sm text-slate-700"
              >
                <span className="flex h-5 w-5 items-center justify-center rounded-full bg-emerald-100 text-emerald-600">
                  <Check className="h-3 w-3" />
                </span>
                {feature}
              </li>
            ))}
          </ul>

          <div className="mt-7 flex items-center gap-3">
            <span className="text-sm font-medium text-slate-600">Quantity</span>
            <div className="flex items-center rounded-xl border border-slate-200 bg-white">
              <button
                type="button"
                onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                className="flex h-10 w-10 items-center justify-center text-slate-600 transition hover:bg-slate-100"
                aria-label="Decrease quantity"
              >
                <Minus className="h-4 w-4" />
              </button>
              <span className="w-10 text-center font-semibold text-slate-900">{quantity}</span>
              <button
                type="button"
                onClick={() => setQuantity((q) => Math.min(product.stock, q + 1))}
                className="flex h-10 w-10 items-center justify-center text-slate-600 transition hover:bg-slate-100"
                aria-label="Increase quantity"
              >
                <Plus className="h-4 w-4" />
              </button>
            </div>
            <span className="ml-auto inline-flex items-center gap-1.5 text-sm font-medium text-emerald-600">
              <Check className="h-4 w-4" />
              In stock ({product.stock} left)
            </span>
          </div>

          <div className="mt-6 flex flex-col gap-3 sm:flex-row">
            <button
              type="button"
              onClick={handleAddToCart}
              className="inline-flex flex-1 items-center justify-center gap-2 rounded-xl border-2 border-slate-900 bg-white px-6 py-3.5 text-sm font-semibold text-slate-900 transition hover:bg-slate-50"
            >
              <ShoppingBag className="h-4 w-4" />
              Add to cart
            </button>
            <button
              type="button"
              onClick={handleBuyNow}
              className="inline-flex flex-1 items-center justify-center gap-2 rounded-xl bg-brand-600 px-6 py-3.5 text-sm font-semibold text-white shadow-soft-lg transition hover:bg-brand-500"
            >
              Buy now
            </button>
          </div>

          <div className="mt-8 grid gap-3 rounded-2xl border border-slate-200 bg-white p-5 sm:grid-cols-3">
            <div className="flex items-center gap-2.5 text-sm text-slate-600">
              <Truck className="h-5 w-5 text-brand-500" />
              Free shipping over $75
            </div>
            <div className="flex items-center gap-2.5 text-sm text-slate-600">
              <ShieldCheck className="h-5 w-5 text-brand-500" />
              2-year warranty
            </div>
            <div className="flex items-center gap-2.5 text-sm text-slate-600">
              <RotateCcw className="h-5 w-5 text-brand-500" />
              30-day returns
            </div>
          </div>
        </div>
      </div>

      <section id="reviews" className="mt-16">
        <h2 className="text-2xl font-bold tracking-tight text-slate-900">You may also like</h2>
        {related.length > 0 ? (
          <div className="mt-6 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {related.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        ) : (
          <div className="mt-6 rounded-2xl border border-dashed border-slate-300 bg-white py-16 text-center">
            <p className="text-4xl">✨</p>
            <p className="mt-3 font-semibold text-slate-900">No similar products yet</p>
            <p className="mt-1 text-sm text-slate-500">Check out other categories in the shop.</p>
          </div>
        )}
      </section>
    </div>
  );
}
