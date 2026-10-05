import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { Coffee, Menu, X, ShoppingBag } from 'lucide-react'

const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false)
  const location = useLocation()

  const navLinks = [
    { name: 'Home', path: '/' },
    { name: 'Menu', path: '/menu' },
    { name: 'About', path: '/about' },
    { name: 'Contact', path: '/contact' },
  ]

  return (
    <nav className="bg-stone-900 bg-opacity-95 backdrop-blur-sm sticky top-0 z-50 shadow-lg">
      <div className="container-custom section-padding py-4">
        <div className="flex justify-between items-center">
          <Link to="/" className="flex items-center space-x-2 text-amber-400">
            <Coffee size={32} />
            <span className="text-xl font-serif font-bold">Coffee Haven</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex space-x-8">
            {navLinks.map((link) => (
              <Link
                key={link.name}
                to={link.path}
                className={`text-stone-300 hover:text-amber-400 transition-colors duration-200 font-medium ${
                  location.pathname === link.path ? 'text-amber-400' : ''
                }`}
              >
                {link.name}
              </Link>
            ))}
          </div>

          <div className="hidden md:flex items-center space-x-4">
            <button className="p-2 text-stone-300 hover:text-amber-400 transition-colors">
              <ShoppingBag size={24} />
            </button>
            <button className="btn-primary text-sm">Order Now</button>
          </div>

          {/* Mobile Menu Button */}
          <div className="md:hidden flex items-center space-x-2">
            <button className="p-2 text-stone-300 hover:text-amber-400">
              <ShoppingBag size={24} />
            </button>
            <button
              onClick={() => setIsOpen(!isOpen)}
              className="p-2 text-stone-300 hover:text-amber-400"
            >
              {isOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>
        </div>

        {/* Mobile Menu */}
        {isOpen && (
          <div className="md:hidden mt-4 pb-4 border-t border-stone-700">
            <div className="flex flex-col space-y-3 pt-4">
              {navLinks.map((link) => (
                <Link
                  key={link.name}
                  to={link.path}
                  onClick={() => setIsOpen(false)}
                  className={`text-stone-300 hover:text-amber-400 transition-colors duration-200 py-2 ${
                    location.pathname === link.path ? 'text-amber-400' : ''
                  }`}
                >
                  {link.name}
                </Link>
              ))}
              <button className="btn-primary text-sm mt-2">Order Now</button>
            </div>
          </div>
        )}
      </div>
    </nav>
  )
}

export default Navbar