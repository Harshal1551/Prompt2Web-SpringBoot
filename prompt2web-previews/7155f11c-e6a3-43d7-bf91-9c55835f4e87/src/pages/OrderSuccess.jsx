import { Link, useLocation, useNavigate } from 'react-router-dom';
import { CheckCircle2, Package, ArrowRight, PartyPopper } from 'lucide-react';
import { formatPrice } from '../data/products.js';

export default function OrderSuccess() {
  const location = useLocation();
  const navigate = useNavigate();
  const state = location.state;

  const order = state?.number
    ? {
        number: state.number,
        total: state.total,
        items: state.items || []
      }
    : (() => {
        try {
          const raw = sessionStorage.getItem('nova-last-order');
          return raw ? JSON.parse(raw) : null;
        } catch (error) {
          return null;
        }
      })();

  return (
    <div className="mx-auto max-w-3xl px-4 py-16 sm:px-6">
      <div className="rounded-3xl border border-slate-200 bg-white p-8 text-center shadow-sm sm:p-12">
        <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-emerald-100 text-emerald-600">
          <CheckCircle2 className="h-10 w-10" />
        </div>
        <div className="mt-6 flex items-center justify-center gap-2 text-brand-600">
          <PartyPopper className="h-5 w-5" />
          <span className="text-xs font-bold uppercase tracking-widest">Order confirmed</span>
        </div>
        <h1 className="mt-3 text-3xl font-bold tracking-tight text-slate-900">Thank you for your order!</h1>
        <p className="mt-3 text-slate-500">
          {order ? (
            <>
              Order <span className="font-semibold text-slate-900">#{order.number}</span> has been placed. A confirmation email is on its way.
            </>
          ) : (
            'Your order has been placed. A confirmation email is on its way.'
          )}
        </p>

        {order && order.items?.length > 0 && (
          <div className="mt-8 rounded-2xl border border-slate-200 bg-slate-50 p-5 text-left">
            <h2 className="flex items-center gap-2 text-sm font-bold text-slate-900">
              <Package className="h-4 w-4 text-brand-600" />
              Your items
            </h2>
            <ul className="mt-4 space-y-3">
              {order.items.map((item) => (
                <li key={item.id} className="flex items-center gap-3">
                  <div
                    className={`flex h-11 w-11 shrink-0 items-center justify-center rounded-lg bg-gradient-to-br ${item.gradient}`}
                  >
                    <span className="text-xl" aria-hidden="true">
                      {item.emoji}
                    </span>
                  </div>
                  <div className="min-w-0 flex-1">
                    <p className="line-clamp-1 text-sm font-semibold text-slate-900">{item.name}</p>
                    <p className="text-xs text-slate-500">Qty: {item.quantity}</p>
                  </div>
                  <p className="text-sm font-bold text-slate-900">{formatPrice(item.price * item.quantity)}</p>
                </li>
              ))}
            </ul>
            <div className="mt-4 flex justify-between border-t border-slate-200 pt-4 text-sm font-bold text-slate-900">
              <span>Total paid</span>
              <span>{formatPrice(order.total)}</span>
            </div>
          </div>
        )}

        <div className="mt-8 flex flex-col gap-3 sm:flex-row sm:justify-center">
          <button
            type="button"
            onClick={() => navigate('/shop')}
            className="inline-flex items-center justify-center gap-2 rounded-xl bg-brand-600 px-6 py-3 text-sm font-semibold text-white shadow-soft-lg transition hover:bg-brand-500"
          >
            Continue shopping
            <ArrowRight className="h-4 w-4" />
          </button>
          <Link
            to="/"
            className="inline-flex items-center justify-center rounded-xl border border-slate-200 bg-white px-6 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
          >
            Back to home
          </Link>
        </div>
      </div>
    </div>
  );
}
