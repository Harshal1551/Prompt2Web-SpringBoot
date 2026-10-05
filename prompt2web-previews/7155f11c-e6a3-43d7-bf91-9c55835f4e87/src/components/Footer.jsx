import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Sparkles, Send, X, MapPin, Phone, Mail } from 'lucide-react';

const SHOP_LINKS = ['Audio', 'Wearables', 'Home', 'Office', 'All products'];
const COMPANY_LINKS = ['About us', 'Careers', 'Press', 'Sustainability', 'Contact'];

export default function Footer() {
  const [email, setEmail] = useState('');
  const [subscribed, setSubscribed] = useState(false);

  const handleSubscribe = (event) => {
    event.preventDefault();
    if (!email.trim()) return;
    setSubscribed(true);
    setEmail('');
  };

  return (
    <footer className="mt-20 bg-slate-900 text-slate-300">
      <div className="mx-auto grid max-w-7xl gap-10 px-4 py-14 sm:px-6 md:grid-cols-2 lg:grid-cols-5 lg:px-8">
        <div className="lg:col-span-2">
          <Link to="/" className="flex items-center gap-2">
            <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-brand-500 to-fuchsia-500 text-white">
              <Sparkles className="h-5 w-5" />
            </span>
            <span className="text-xl font-bold tracking-tight text-white">
              NOVA<span className="text-brand-400">.</span>
            </span>
          </Link>
          <p className="mt-4 max-w-sm text-sm leading-6 text-slate-400">
            Modern essentials for work, home and everywhere in between. Designed with care, delivered with speed.
          </p>
          <form onSubmit={handleSubscribe} className="mt-6 max-w-sm">
            <label htmlFor="newsletter" className="text-xs font-semibold uppercase tracking-wider text-slate-400">
              Get 10% off your first order
            </label>
            <div className="mt-2 flex items-center rounded-xl border border-slate-700 bg-slate-800 p-1.5 focus-within:border-brand-400">
              <input
                id="newsletter"
                type="email"
                required
                value={email}
                onChange={(event) => {
                  setEmail(event.target.value);
                  setSubscribed(false);
                }}
                placeholder="you@example.com"
                className="w-full bg-transparent px-3 text-sm text-white placeholder:text-slate-500 focus:outline-none"
              />
              <button
                type="submit"
                className="inline-flex items-center gap-1.5 rounded-lg bg-brand-500 px-3.5 py-2 text-sm font-semibold text-white transition hover:bg-brand-400"
              >
                <Send className="h-4 w-4" />
                Join
              </button>
            </div>
            {subscribed && (
              <p className="mt-2 flex items-center gap-1.5 text-xs text-emerald-400">
                <X className="h-3 w-3 rotate-45" />
                You are on the list. Check your inbox for the code.
              </p>
            )}
          </form>
        </div>

        <div>
          <h3 className="text-sm font-semibold uppercase tracking-wider text-white">Shop</h3>
          <ul className="mt-4 space-y-2.5 text-sm">
            {SHOP_LINKS.map((label) => (
              <li key={label}>
                <Link
                  to={label === 'All products' ? '/shop' : `/shop?category=${encodeURIComponent(label)}`}
                  className="text-slate-400 transition hover:text-white"
                >
                  {label}
                </Link>
              </li>
            ))}
          </ul>
        </div>

        <div>
          <h3 className="text-sm font-semibold uppercase tracking-wider text-white">Company</h3>
          <ul className="mt-4 space-y-2.5 text-sm">
            {COMPANY_LINKS.map((label) => (
              <li key={label}>
                <Link to="/" className="text-slate-400 transition hover:text-white">
                  {label}
                </Link>
              </li>
            ))}
          </ul>
        </div>

        <div>
          <h3 className="text-sm font-semibold uppercase tracking-wider text-white">Visit us</h3>
          <ul className="mt-4 space-y-3 text-sm text-slate-400">
            <li className="flex items-start gap-2.5">
              <MapPin className="mt-0.5 h-4 w-4 shrink-0 text-brand-400" />
              128 Lumen Avenue, Suite 400<br />San Francisco, CA 94105
            </li>
            <li className="flex items-center gap-2.5">
              <Phone className="h-4 w-4 shrink-0 text-brand-400" />
              +1 (800) 555-0147
            </li>
            <li className="flex items-center gap-2.5">
              <Mail className="h-4 w-4 shrink-0 text-brand-400" />
              hello@nova-store.example
            </li>
          </ul>
        </div>
      </div>

      <div className="border-t border-slate-800">
        <div className="mx-auto flex max-w-7xl flex-col items-center justify-between gap-4 px-4 py-6 text-xs text-slate-500 sm:flex-row sm:px-6 lg:px-8">
          <p>© 2025 NOVA Store. All rights reserved.</p>
          <div className="flex gap-5">
            <Link to="/" className="transition hover:text-slate-300">Privacy Policy</Link>
            <Link to="/" className="transition hover:text-slate-300">Terms of Service</Link>
            <Link to="/" className="transition hover:text-slate-300">Cookies</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}
