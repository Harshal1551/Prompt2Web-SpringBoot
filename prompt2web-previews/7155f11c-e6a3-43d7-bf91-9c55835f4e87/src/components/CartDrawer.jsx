import { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { X, Plus, Minus, Trash2, ShoppingBag, ArrowRight } from 'lucide-react';
import { useCart } from '../context/CartContext.jsx';
import { products, formatPrice } from '../data/products.js';

export default function CartDrawer() {
  const { items, isOpen, closeCart, setQuantity, removeFromCart } = useCart();
  const navigate = useNavigate();

  const resolved = items
    .map((item) => ({ ...item, product: products.find((p) => p.id === item.id) }))
    .filter((item) => item.product);

  const subtotal = resolved.reduce((sum, item) => sum + item.product.price * item.quantity, 0);
  const shipping = subtotal === 0 || subtotal >= 75 ? 0 : 6.99;
  const total = subtotal + shipping;
  const count = resolved.reduce((sum, item) => sum + item.quantity, 0);

  useEffect(() => {
    const onKey = (event) => {
      if (event.key === 'Escape') closeCart();
    };
    if (isOpen) {
      window.addEventListener('keydown', onKey);
      document.body.style.overflow = 'hidden';
    }
    return () => {
      window.removeEventListener('keydown', onKey);
      document.body.style.overflow = '';
    };
  }, [isOpen, closeCart]);

  return (
    <div aria-hidden={!isOpen} className={`fixed inset-0 z-50 ${isOpen ? '' : 'pointer-events-none'}`}>
      <div
        onClick={closeCart}
        className={`absolute inset-0 bg-slate-900/50 transition-opacity duration-300 ${
          isOpen ? 'opacity-100' : 'opacity-0'
        }`}
      />

      <aside
        role="dialog"
        aria-modal="true"
        aria-label="Shopping cart"
        className={`absolute right-0 top-0 flex h-full w-full max-w-md flex-col bg-white shadow-2xl transition-transform duration-300 ${
          isOpen ? 'translate-x-0' : 'translate-x-full'
        }`}
      >
        <div className="flex items-center justify-between border-b border-slate-200 px-5 py-4">
          <h2 className="flex items-center gap-2 text-lg font-bold text-slate-900">
            <ShoppingBag className="h-5 w-5 text-brand-600" />
            Your Cart
            {count > 0 && (
              <span className="rounded-full bg-brand-100 px-2 py-0.5 text-xs font-semibold text-brand-700">
                {count}
              </span>
            )}
          </h2>
          <button
            type="button"
            onClick={closeCart}
            className="inline-flex h-9 w-9 items-center justify-center rounded-xl text-slate-500 transition hover:bg-slate-100"
            aria-label="Close cart"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {resolved.length === 0 ? (
          <div className="flex flex-1 flex-col items-center justify-center gap-4 px-6 text-center">
            <div className="flex h-20 w-20 items-center justify-center rounded-full bg-slate-100 text-4xl">🛍️</div>
            <div>
              <p className="text-lg font-semibold text-slate-900">Your cart is empty</p>
              <p className="mt-1 text-sm text-slate-500">Discover something you will love.</p>
            </div>
            <button
              type="button"
              onClick={() => {
                closeCart();
                navigate('/shop');
              }}
              className="inline-flex items-center gap-2 rounded-xl bg-brand-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-brand-500"
            >
              Browse products
              <ArrowRight className="h-4 w-4" />
            </button>
          </div>
        ) : (
          <>
            <ul className="flex-1 divide-y divide-slate-100 overflow-y-auto px-5 no-scrollbar">
              {resolved.map(({ product, quantity }) => (
                <li key={product.id} className="flex gap-4 py-4">
                  <Link
                    to={`/product/${product.id}`}
                    onClick={closeCart}
                    className={`flex h-20 w-20 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br ${product.gradient}`}
                  >
                    <span className="text-3xl" aria-hidden="true">
                      {product.emoji}
                    </span>
                  </Link>
                  <div className="flex min-w-0 flex-1 flex-col">
                    <div className="flex items-start justify-between gap-2">
                      <Link
                        to={`/product/${product.id}`}
                        onClick={closeCart}
                        className="line-clamp-1 text-sm font-semibold text-slate-900 hover:text-brand-700"
                      >
                        {product.name}
                      </Link>
                      <button
                        type="button"
                        onClick={() => removeFromCart(product.id)}
                        className="text-slate-400 transition hover:text-rose-500"
                        aria-label={`Remove ${product.name}`}
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                    <p className="mt-0.5 text-xs text-slate-500">{formatPrice(product.price)} each</p>
                    <div className="mt-auto flex items-center justify-between pt-2">
                      <div className="flex items-center rounded-lg border border-slate-200">
                        <button
                          type="button"
                          onClick={() => setQuantity(product.id, quantity - 1)}
                          className="flex h-8 w-8 items-center justify-center text-slate-600 transition hover:bg-slate-100"
                          aria-label="Decrease quantity"
                        >
                          <Minus className="h-3.5 w-3.5" />
                        </button>
                        <span className="w-8 text-center text-sm font-semibold text-slate-900">{quantity}</span>
                        <button
                          type="button"
                          onClick={() => setQuantity(product.id, quantity + 1)}
                          className="flex h-8 w-8 items-center justify-center text-slate-600 transition hover:bg-slate-100"
                          aria-label="Increase quantity"
                        >
                          <Plus className="h-3.5 w-3.5" />
                        </button>
                      </div>
                      <p className="text-sm font-bold text-slate-900">
                        {formatPrice(product.price * quantity)}
                      </p>
                    </div>
                  </div>
                </li>
              ))}
            </ul>

            <div className="border-t border-slate-200 bg-slate-50 px-5 py-4">
              <dl className="space-y-1.5 text-sm">
                <div className="flex justify-between text-slate-600">
                  <dt>Subtotal</dt>
                  <dd className="font-medium text-slate-900">{formatPrice(subtotal)}</dd>
                </div>
                <div className="flex justify-between text-slate-600">
                  <dt>Shipping</dt>
                  <dd className="font-medium text-slate-900">
                    {shipping === 0 ? <span className="text-emerald-600">Free</span> : formatPrice(shipping)}
                  </dd>
                </div>
                <div className="flex justify-between border-t border-slate-200 pt-2 text-base font-bold text-slate-900">
                  <dt>Total</dt>
                  <dd>{formatPrice(total)}</dd>
                </div>
              </dl>
              {shipping > 0 && (
                <p className="mt-2 text-xs text-slate-500">
                  Add {formatPrice(75 - subtotal)} more for free shipping.
                </p>
              )}
              <button
                type="button"
                onClick={() => {
                  closeCart();
                  navigate('/checkout');
                }}
                className="mt-4 flex w-full items-center justify-center gap-2 rounded-xl bg-brand-600 py-3.5 text-sm font-semibold text-white shadow-soft-lg transition hover:bg-brand-500"
              >
                Checkout
                <ArrowRight className="h-4 w-4" />
              </button>
              <button
                type="button"
                onClick={closeCart}
                className="mt-2 w-full rounded-xl py-2.5 text-sm font-medium text-slate-600 transition hover:bg-slate-100"
              >
                Continue shopping
              </button>
            </div>
          </>
        )}
      </aside>
    </div>
  );
}
