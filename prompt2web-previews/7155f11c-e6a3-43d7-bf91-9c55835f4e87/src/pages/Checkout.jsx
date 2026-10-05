import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import {
  ShoppingBag,
  Truck,
  ArrowLeft,
  CheckCircle2,
  CreditCard,
  Lock,
  Minus,
  Plus,
  Trash2
} from 'lucide-react';
import { products, formatPrice } from '../data/products.js';
import { useCart } from '../context/CartContext.jsx';

const DELIVERY_OPTIONS = [
  { id: 'standard', label: 'Standard delivery', eta: '3-5 business days', cost: 0 },
  { id: 'express', label: 'Express delivery', eta: '1-2 business days', cost: 9.99 },
  { id: 'overnight', label: 'Overnight delivery', eta: 'By tomorrow', cost: 19.99 }
];

export default function Checkout() {
  const navigate = useNavigate();
  const location = useLocation();
  const { items, setQuantity, removeFromCart, clearCart } = useCart();

  const [form, setForm] = useState({
    name: '',
    email: '',
    address: '',
    city: '',
    zip: '',
    country: '',
    card: '',
    exp: '',
    cvc: ''
  });
  const [delivery, setDelivery] = useState('standard');
  const [touched, setTouched] = useState(false);

  const resolved = items
    .map((item) => ({ ...item, product: products.find((p) => p.id === item.id) }))
    .filter((item) => item.product);

  const subtotal = resolved.reduce((sum, item) => sum + item.product.price * item.quantity, 0);
  const selectedDelivery = DELIVERY_OPTIONS.find((d) => d.id === delivery);
  const deliveryCost = subtotal >= 75 && delivery === 'standard' ? 0 : selectedDelivery.cost;
  const total = subtotal + deliveryCost;

  const fieldErrors = {
    name: form.name.trim() ? null : 'Required',
    email: /.+@.+/i.test(form.email) ? null : 'Valid email required',
    address: form.address.trim() ? null : 'Required',
    city: form.city.trim() ? null : 'Required',
    zip: form.zip.trim() ? null : 'Required',
    country: form.country.trim() ? null : 'Required',
    card: form.card.replace(/\s/g, '').length >= 12 ? null : 'Card number required',
    exp: form.exp.trim() ? null : 'Required',
    cvc: form.cvc.trim().length >= 3 ? null : 'CVC required'
  };
  const hasErrors = Object.values(fieldErrors).some(Boolean);

  const updateField = (key) => (event) => {
    setForm((prev) => ({ ...prev, [key]: event.target.value }));
  };

  const handlePlaceOrder = (event) => {
    event.preventDefault();
    setTouched(true);
    if (hasErrors || resolved.length === 0) return;

    const orderNumber = `NV-${Date.now().toString(36).toUpperCase()}`;
    const snapshot = resolved.map((item) => ({
      id: item.product.id,
      name: item.product.name,
      price: item.product.price,
      quantity: item.quantity,
      emoji: item.product.emoji,
      gradient: item.product.gradient
    }));

    sessionStorage.setItem(
      'nova-last-order',
      JSON.stringify({ number: orderNumber, total, email: form.email, items: snapshot })
    );

    clearCart();
    navigate('/order-success', { state: { number: orderNumber, total } });
  };

  const inputClass = (key) =>
    `w-full rounded-xl border bg-white px-3.5 py-2.5 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 ${
      touched && fieldErrors[key]
        ? 'border-rose-300 focus:border-rose-400 focus:ring-rose-100'
        : 'border-slate-200 focus:border-brand-400 focus:ring-brand-100'
    }`;

  if (location.state?.placed && resolved.length === 0) {
    return (
      <div className="mx-auto max-w-2xl px-4 py-24 text-center sm:px-6">
        <div className="text-6xl">🛍️</div>
        <h1 className="mt-6 text-2xl font-bold text-slate-900">Your cart is empty</h1>
        <p className="mt-2 text-slate-500">Add some products before checking out.</p>
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

  if (resolved.length === 0) {
    return (
      <div className="mx-auto max-w-2xl px-4 py-24 text-center sm:px-6">
        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-slate-100 text-slate-400 mx-auto">
          <ShoppingBag className="h-7 w-7" />
        </div>
        <h1 className="mt-6 text-2xl font-bold text-slate-900">Your cart is empty</h1>
        <p className="mt-2 text-slate-500">Add some products before checking out.</p>
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

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
      <button
        type="button"
        onClick={() => navigate(-1)}
        className="inline-flex items-center gap-1.5 text-sm font-medium text-slate-500 transition hover:text-slate-900"
      >
        <ArrowLeft className="h-4 w-4" />
        Back
      </button>

      <h1 className="mt-4 text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">Checkout</h1>

      <form onSubmit={handlePlaceOrder} className="mt-8 grid gap-10 lg:grid-cols-[1fr_400px]">
        <div className="space-y-8">
          <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="flex items-center gap-2 text-lg font-bold text-slate-900">
              <CheckCircle2 className="h-5 w-5 text-brand-600" />
              Contact & shipping
            </h2>
            <div className="mt-5 grid gap-4 sm:grid-cols-2">
              <div className="sm:col-span-2">
                <label htmlFor="name" className="text-sm font-medium text-slate-700">Full name</label>
                <input
                  id="name"
                  type="text"
                  value={form.name}
                  onChange={updateField('name')}
                  placeholder="Alex Johnson"
                  className={`mt-1.5 ${inputClass('name')}`}
                />
                {touched && fieldErrors.name && <p className="mt-1 text-xs text-rose-500">{fieldErrors.name}</p>}
              </div>
              <div className="sm:col-span-2">
                <label htmlFor="email" className="text-sm font-medium text-slate-700">Email</label>
                <input
                  id="email"
                  type="email"
                  value={form.email}
                  onChange={updateField('email')}
                  placeholder="alex@example.com"
                  className={`mt-1.5 ${inputClass('email')}`}
                />
                {touched && fieldErrors.email && <p className="mt-1 text-xs text-rose-500">{fieldErrors.email}</p>}
              </div>
              <div className="sm:col-span-2">
                <label htmlFor="address" className="text-sm font-medium text-slate-700">Street address</label>
                <input
                  id="address"
                  type="text"
                  value={form.address}
                  onChange={updateField('address')}
                  placeholder="128 Lumen Avenue, Apt 4"
                  className={`mt-1.5 ${inputClass('address')}`}
                />
                {touched && fieldErrors.address && <p className="mt-1 text-xs text-rose-500">{fieldErrors.address}</p>}
              </div>
              <div>
                <label htmlFor="city" className="text-sm font-medium text-slate-700">City</label>
                <input
                  id="city"
                  type="text"
                  value={form.city}
                  onChange={updateField('city')}
                  placeholder="San Francisco"
                  className={`mt-1.5 ${inputClass('city')}`}
                />
                {touched && fieldErrors.city && <p className="mt-1 text-xs text-rose-500">{fieldErrors.city}</p>}
              </div>
              <div>
                <label htmlFor="zip" className="text-sm font-medium text-slate-700">ZIP / Postal code</label>
                <input
                  id="zip"
                  type="text"
                  value={form.zip}
                  onChange={updateField('zip')}
                  placeholder="94105"
                  className={`mt-1.5 ${inputClass('zip')}`}
                />
                {touched && fieldErrors.zip && <p className="mt-1 text-xs text-rose-500">{fieldErrors.zip}</p>}
              </div>
              <div className="sm:col-span-2">
                <label htmlFor="country" className="text-sm font-medium text-slate-700">Country</label>
                <input
                  id="country"
                  type="text"
                  value={form.country}
                  onChange={updateField('country')}
                  placeholder="United States"
                  className={`mt-1.5 ${inputClass('country')}`}
                />
                {touched && fieldErrors.country && <p className="mt-1 text-xs text-rose-500">{fieldErrors.country}</p>}
              </div>
            </div>
          </section>

          <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="flex items-center gap-2 text-lg font-bold text-slate-900">
              <Truck className="h-5 w-5 text-brand-600" />
              Delivery method
            </h2>
            <div className="mt-5 space-y-3">
              {DELIVERY_OPTIONS.map((option) => (
                <label
                  key={option.id}
                  className={`flex cursor-pointer items-center justify-between rounded-xl border p-4 transition ${
                    delivery === option.id
                      ? 'border-brand-500 bg-brand-50/50 ring-2 ring-brand-100'
                      : 'border-slate-200 hover:border-slate-300'
                  }`}
                >
                  <span className="flex items-center gap-3">
                    <input
                      type="radio"
                      name="delivery"
                      checked={delivery === option.id}
                      onChange={() => setDelivery(option.id)}
                      className="h-4 w-4 accent-brand-600"
                    />
                    <span>
                      <span className="block text-sm font-semibold text-slate-900">{option.label}</span>
                      <span className="block text-xs text-slate-500">{option.eta}</span>
                    </span>
                  </span>
                  <span className="text-sm font-semibold text-slate-900">
                    {subtotal >= 75 && option.id === 'standard' ? 'Free' : formatPrice(option.cost)}
                  </span>
                </label>
              ))}
            </div>
          </section>

          <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="flex items-center gap-2 text-lg font-bold text-slate-900">
              <CreditCard className="h-5 w-5 text-brand-600" />
              Payment details
            </h2>
            <div className="mt-5 grid gap-4 sm:grid-cols-2">
              <div className="sm:col-span-2">
                <label htmlFor="card" className="text-sm font-medium text-slate-700">Card number</label>
                <input
                  id="card"
                  inputMode="numeric"
                  value={form.card}
                  onChange={updateField('card')}
                  placeholder="4242 4242 4242 4242"
                  className={`mt-1.5 ${inputClass('card')}`}
                />
                {touched && fieldErrors.card && <p className="mt-1 text-xs text-rose-500">{fieldErrors.card}</p>}
              </div>
              <div>
                <label htmlFor="exp" className="text-sm font-medium text-slate-700">Expiry (MM/YY)</label>
                <input
                  id="exp"
                  value={form.exp}
                  onChange={updateField('exp')}
                  placeholder="12/27"
                  className={`mt-1.5 ${inputClass('exp')}`}
                />
                {touched && fieldErrors.exp && <p className="mt-1 text-xs text-rose-500">{fieldErrors.exp}</p>}
              </div>
              <div>
                <label htmlFor="cvc" className="text-sm font-medium text-slate-700">CVC</label>
                <input
                  id="cvc"
                  inputMode="numeric"
                  value={form.cvc}
                  onChange={updateField('cvc')}
                  placeholder="123"
                  className={`mt-1.5 ${inputClass('cvc')}`}
                />
                {touched && fieldErrors.cvc && <p className="mt-1 text-xs text-rose-500">{fieldErrors.cvc}</p>}
              </div>
            </div>
            <p className="mt-4 flex items-center gap-1.5 text-xs text-slate-400">
              <Lock className="h-3.5 w-3.5" />
              This is a demo store. No real payment will be processed.
            </p>
          </section>
        </div>

        <aside className="lg:sticky lg:top-24 lg:h-fit">
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-lg font-bold text-slate-900">Order summary</h2>
            <ul className="mt-5 max-h-72 space-y-4 overflow-y-auto pr-1 no-scrollbar">
              {resolved.map(({ product, quantity }) => (
                <li key={product.id} className="flex items-center gap-3">
                  <div
                    className={`relative flex h-14 w-14 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br ${product.gradient}`}
                  >
                    <span className="text-2xl" aria-hidden="true">
                      {product.emoji}
                    </span>
                    <span className="absolute -right-2 -top-2 flex h-5 min-w-[1.25rem] items-center justify-center rounded-full bg-slate-900 px-1 text-[11px] font-bold text-white">
                      {quantity}
                    </span>
                  </div>
                  <div className="min-w-0 flex-1">
                    <p className="line-clamp-1 text-sm font-semibold text-slate-900">{product.name}</p>
                    <p className="text-xs text-slate-500">{formatPrice(product.price)} each</p>
                  </div>
                  <div className="flex flex-col items-end gap-1">
                    <p className="text-sm font-bold text-slate-900">
                      {formatPrice(product.price * quantity)}
                    </p>
                    <div className="flex items-center gap-1">
                      <button
                        type="button"
                        onClick={() => setQuantity(product.id, quantity - 1)}
                        className="flex h-6 w-6 items-center justify-center rounded-md text-slate-500 hover:bg-slate-100"
                        aria-label="Decrease quantity"
                      >
                        <Minus className="h-3 w-3" />
                      </button>
                      <button
                        type="button"
                        onClick={() => setQuantity(product.id, quantity + 1)}
                        className="flex h-6 w-6 items-center justify-center rounded-md text-slate-500 hover:bg-slate-100"
                        aria-label="Increase quantity"
                      >
                        <Plus className="h-3 w-3" />
                      </button>
                      <button
                        type="button"
                        onClick={() => removeFromCart(product.id)}
                        className="flex h-6 w-6 items-center justify-center rounded-md text-slate-400 hover:bg-rose-50 hover:text-rose-500"
                        aria-label="Remove item"
                      >
                        <Trash2 className="h-3 w-3" />
                      </button>
                    </div>
                  </div>
                </li>
              ))}
            </ul>

            <dl className="mt-6 space-y-1.5 border-t border-slate-100 pt-4 text-sm">
              <div className="flex justify-between text-slate-600">
                <dt>Subtotal</dt>
                <dd className="font-medium text-slate-900">{formatPrice(subtotal)}</dd>
              </div>
              <div className="flex justify-between text-slate-600">
                <dt>Delivery</dt>
                <dd className="font-medium text-slate-900">
                  {deliveryCost === 0 ? <span className="text-emerald-600">Free</span> : formatPrice(deliveryCost)}
                </dd>
              </div>
              <div className="flex justify-between border-t border-slate-100 pt-2 text-base font-bold text-slate-900">
                <dt>Total</dt>
                <dd>{formatPrice(total)}</dd>
              </div>
            </dl>

            <button
              type="submit"
              disabled={touched && hasErrors}
              className="mt-6 w-full rounded-xl bg-brand-600 py-3.5 text-sm font-semibold text-white shadow-soft-lg transition hover:bg-brand-500 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Place order · {formatPrice(total)}
            </button>
            <p className="mt-3 text-center text-xs text-slate-400">
              By placing an order you agree to our Terms of Service and Privacy Policy.
            </p>
          </div>
        </aside>
      </form>
    </div>
  );
}
