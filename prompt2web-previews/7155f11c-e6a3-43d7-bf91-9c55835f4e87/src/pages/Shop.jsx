import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { SearchX, SlidersHorizontal } from 'lucide-react';
import { products, CATEGORIES } from '../data/products.js';
import ProductCard from '../components/ProductCard.jsx';

const SORTS = [
  { id: 'featured', label: 'Featured' },
  { id: 'price-asc', label: 'Price: Low to High' },
  { id: 'price-desc', label: 'Price: High to Low' },
  { id: 'rating', label: 'Top Rated' }
];

export default function Shop() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [sort, setSort] = useState('featured');
  const [maxPrice, setMaxPrice] = useState(250);

  const query = (searchParams.get('q') || '').toLowerCase();
  const category = searchParams.get('category') || 'All';

  useEffect(() => {
    if (category !== 'All' && !CATEGORIES.includes(category)) {
      setSearchParams({ q: query || undefined }, { replace: true });
    }
  }, [category, query, searchParams, setSearchParams]);

  const updateParam = (key, value) => {
    const next = new URLSearchParams(searchParams);
    if (value) next.set(key, value);
    else next.delete(key);
    setSearchParams(next, { replace: true });
  };

  const filtered = useMemo(() => {
    let list = products.filter((product) => product.price <= maxPrice);
    if (category !== 'All') {
      list = list.filter((product) => product.category === category);
    }
    if (query) {
      list = list.filter(
        (product) =>
          product.name.toLowerCase().includes(query) ||
          product.category.toLowerCase().includes(query) ||
          product.tagline.toLowerCase().includes(query)
      );
    }
    switch (sort) {
      case 'price-asc':
        return [...list].sort((a, b) => a.price - b.price);
      case 'price-desc':
        return [...list].sort((a, b) => b.price - a.price);
      case 'rating':
        return [...list].sort((a, b) => b.rating - a.rating);
      default:
        return list;
    }
  }, [query, category, sort, maxPrice]);

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
            {category === 'All' ? 'All products' : category}
          </h1>
          <p className="mt-1.5 text-slate-500">
            {filtered.length} {filtered.length === 1 ? 'product' : 'products'}
            {query ? <> matching “{query}”</> : null}
          </p>
        </div>
      </div>

      <div className="mt-8 grid gap-8 lg:grid-cols-[260px_1fr]">
        <aside className="space-y-7 lg:sticky lg:top-24 lg:h-fit">
          <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm lg:hidden">
            <h2 className="flex items-center gap-2 text-sm font-bold uppercase tracking-wider text-slate-500">
              <SlidersHorizontal className="h-4 w-4" />
              Filters
            </h2>
          </div>

          <div className="hidden rounded-2xl border border-slate-200 bg-white p-5 shadow-sm lg:block">
            <h2 className="flex items-center gap-2 text-sm font-bold uppercase tracking-wider text-slate-500">
              <SlidersHorizontal className="h-4 w-4" />
              Filters
            </h2>

            <div className="mt-5">
              <h3 className="text-sm font-semibold text-slate-900">Category</h3>
              <div className="mt-3 space-y-1.5">
                {CATEGORIES.map((item) => (
                  <button
                    key={item}
                    type="button"
                    onClick={() => updateParam('category', item === 'All' ? null : item)}
                    className={`block w-full rounded-lg px-3 py-2 text-left text-sm transition ${
                      category === item
                        ? 'bg-brand-50 font-semibold text-brand-700'
                        : 'text-slate-600 hover:bg-slate-100'
                    }`}
                  >
                    {item}
                  </button>
                ))}
              </div>
            </div>

            <div className="mt-6 border-t border-slate-100 pt-6">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-semibold text-slate-900">Max price</h3>
                <span className="text-sm font-semibold text-brand-700">${maxPrice}</span>
              </div>
              <input
                type="range"
                min={20}
                max={250}
                step={5}
                value={maxPrice}
                onChange={(event) => setMaxPrice(Number(event.target.value))}
                className="mt-3 w-full accent-brand-600"
              />
            </div>

            <div className="mt-6 border-t border-slate-100 pt-6">
              <h3 className="text-sm font-semibold text-slate-900">Sort by</h3>
              <select
                value={sort}
                onChange={(event) => setSort(event.target.value)}
                className="mt-3 w-full rounded-lg border border-slate-200 bg-white px-3 py-2.5 text-sm text-slate-700 focus:border-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-100"
              >
                {SORTS.map((option) => (
                  <option key={option.id} value={option.id}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </aside>

        <div>
          <div className="mb-6 flex gap-3 overflow-x-auto pb-2 no-scrollbar lg:hidden">
            {CATEGORIES.map((item) => (
              <button
                key={item}
                type="button"
                onClick={() => updateParam('category', item === 'All' ? null : item)}
                className={`shrink-0 rounded-xl px-4 py-2 text-sm font-medium transition ${
                  category === item
                    ? 'bg-slate-900 text-white'
                    : 'border border-slate-200 bg-white text-slate-600 hover:border-brand-300'
                }`}
              >
                {item}
              </button>
            ))}
          </div>

          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-white py-24 text-center">
              <div className="flex h-16 w-16 items-center justify-center rounded-full bg-slate-100 text-slate-400">
                <SearchX className="h-7 w-7" />
              </div>
              <p className="mt-4 text-lg font-semibold text-slate-900">No products found</p>
              <p className="mt-1 text-sm text-slate-500">Try adjusting your filters or search term.</p>
              <button
                type="button"
                onClick={() => {
                  setSearchParams({}, { replace: true });
                  setMaxPrice(250);
                }}
                className="mt-5 rounded-xl bg-brand-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-brand-500"
              >
                Clear filters
              </button>
            </div>
          ) : (
            <div className="grid gap-6 sm:grid-cols-2 xl:grid-cols-3">
              {filtered.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
