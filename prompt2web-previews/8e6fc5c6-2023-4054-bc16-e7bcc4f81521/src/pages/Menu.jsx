import { useState } from 'react'
import { Coffee, Filter, Search } from 'lucide-react'

const Menu = () => {
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [searchTerm, setSearchTerm] = useState('')

  const categories = ['all', 'espresso', 'latte', 'cold-brew', 'pastry']

  const menuItems = [
    {
      id: 1,
      name: 'Signature Espresso',
      category: 'espresso',
      price: 3.50,
      description: 'Rich, bold, and perfectly balanced espresso shot with notes of dark chocolate and caramel.',
      popular: true,
    },
    {
      id: 2,
      name: 'Double Shot',
      category: 'espresso',
      price: 4.25,
      description: 'Two shots of our signature espresso for an extra kick.',
      popular: false,
    },
    {
      id: 3,
      name: 'Vanilla Latte',
      category: 'latte',
      price: 5.25,
      description: 'Smooth espresso with steamed milk and a hint of natural vanilla syrup.',
      popular: true,
    },
    {
      id: 4,
      name: 'Caramel Macchiato',
      category: 'latte',
      price: 5.75,
      description: 'Espresso, steamed milk, and caramel syrup topped with a caramel drizzle.',
      popular: false,
    },
    {
      id: 5,
      name: 'Cold Brew',
      category: 'cold-brew',
      price: 4.75,
      description: 'Slow-steeped for 24 hours, resulting in a smooth, naturally sweet coffee concentrate.',
      popular: true,
    },
    {
      id: 6,
      name: 'Iced Americano',
      category: 'cold-brew',
      price: 3.95,
      description: 'Espresso poured over ice and topped with cold water.',
      popular: false,
    },
    {
      id: 7,
      name: 'Croissant',
      category: 'pastry',
      price: 3.25,
      description: 'Buttery, flaky French croissant baked fresh daily.',
      popular: true,
    },
    {
      id: 8,
      name: 'Blueberry Muffin',
      category: 'pastry',
      price: 3.50,
      description: 'Moist muffin bursting with fresh blueberries.',
      popular: false,
    },
  ]

  const filteredItems = menuItems.filter((item) => {
    const matchesCategory = selectedCategory === 'all' || item.category === selectedCategory
    const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.description.toLowerCase().includes(searchTerm.toLowerCase())
    return matchesCategory && matchesSearch
  })

  return (
    <div className="section-padding bg-amber-50">
      <div className="container-custom">
        <div className="text-center mb-12">
          <h1 className="text-4xl lg:text-5xl font-serif font-bold text-stone-800 mb-4">Our Menu</h1>
          <p className="text-stone-600 text-lg max-w-2xl mx-auto">
            Explore our carefully crafted selection of coffee and pastries.
          </p>
        </div>

        {/* Search and Filter */}
        <div className="mb-10 flex flex-col md:flex-row gap-4 items-center justify-between">
          <div className="relative w-full md:w-96">
            <Search size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-stone-400" />
            <input
              type="text"
              placeholder="Search menu items..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-3 rounded-lg border border-stone-300 focus:outline-none focus:ring-2 focus:ring-amber-500"
            />
          </div>
          <div className="flex items-center space-x-2">
            <Filter size={20} className="text-stone-600" />
            <span className="font-medium text-stone-700">Filter:</span>
            <div className="flex flex-wrap gap-2">
              {categories.map((category) => (
                <button
                  key={category}
                  onClick={() => setSelectedCategory(category)}
                  className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                    selectedCategory === category
                      ? 'bg-amber-500 text-white'
                      : 'bg-stone-200 text-stone-700 hover:bg-stone-300'
                  }`}
                >
                  {category.charAt(0).toUpperCase() + category.slice(1).replace('-', ' ')}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Menu Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {filteredItems.map((item) => (
            <div key={item.id} className="bg-white rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300">
              <div className="h-48 bg-gradient-to-br from-amber-200 to-amber-300 flex items-center justify-center relative">
                <Coffee size={60} className="text-amber-700" />
                {item.popular && (
                  <span className="absolute top-4 right-4 bg-amber-500 text-white text-xs font-bold px-3 py-1 rounded-full">
                    Popular
                  </span>
                )}
              </div>
              <div className="p-6">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-xl font-serif font-bold text-stone-800">{item.name}</h3>
                  <span className="text-2xl font-bold text-amber-600">${item.price.toFixed(2)}</span>
                </div>
                <p className="text-stone-600 mb-4">{item.description}</p>
                <button className="w-full btn-primary text-sm py-2 px-4">Add to Order</button>
              </div>
            </div>
          ))}
        </div>

        {filteredItems.length === 0 && (
          <div className="text-center py-16">
            <Coffee size={64} className="mx-auto text-stone-400 mb-4" />
            <h3 className="text-xl font-serif font-bold text-stone-800 mb-2">No items found</h3>
            <p className="text-stone-600">Try adjusting your search or filter criteria.</p>
          </div>
        )}
      </div>
    </div>
  )
}

export default Menu