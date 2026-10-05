import { Link } from 'react-router-dom'
import { Coffee, MapPin, Phone, Mail, Clock, Instagram, Facebook, Twitter } from 'lucide-react'

const Footer = () => {
  return (
    <footer className="bg-stone-900 text-stone-300 py-12">
      <div className="container-custom section-padding px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Brand Section */}
          <div className="col-span-1 md:col-span-2">
            <Link to="/" className="flex items-center space-x-2 text-amber-400 mb-4">
              <Coffee size={32} />
              <span className="text-xl font-serif font-bold">Coffee Haven</span>
            </Link>
            <p className="text-stone-400 mb-4 max-w-md">
              Crafting the perfect cup of coffee since 2010. We source the finest beans from around the world and roast them with care to bring you an exceptional coffee experience.
            </p>
            <div className="flex space-x-4">
              <a href="#" className="text-stone-400 hover:text-amber-400 transition-colors">
                <Instagram size={20} />
              </a>
              <a href="#" className="text-stone-400 hover:text-amber-400 transition-colors">
                <Facebook size={20} />
              </a>
              <a href="#" className="text-stone-400 hover:text-amber-400 transition-colors">
                <Twitter size={20} />
              </a>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="text-white font-serif text-lg mb-4">Quick Links</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/" className="hover:text-amber-400 transition-colors">Home</Link>
              </li>
              <li>
                <Link to="/menu" className="hover:text-amber-400 transition-colors">Menu</Link>
              </li>
              <li>
                <Link to="/about" className="hover:text-amber-400 transition-colors">About</Link>
              </li>
              <li>
                <Link to="/contact" className="hover:text-amber-400 transition-colors">Contact</Link>
              </li>
            </ul>
          </div>

          {/* Contact Info */}
          <div>
            <h3 className="text-white font-serif text-lg mb-4">Contact Info</h3>
            <ul className="space-y-3">
              <li className="flex items-start space-x-3">
                <MapPin size={20} className="text-amber-400 mt-1 flex-shrink-0" />
                <span>123 Coffee Street, Brew City, BC 12345</span>
              </li>
              <li className="flex items-center space-x-3">
                <Phone size={20} className="text-amber-400 flex-shrink-0" />
                <span>(555) 123-4567</span>
              </li>
              <li className="flex items-center space-x-3">
                <Mail size={20} className="text-amber-400 flex-shrink-0" />
                <span>hello@coffehaven.com</span>
              </li>
            </ul>
          </div>
        </div>

        {/* Hours & Copyright */}
        <div className="border-t border-stone-800 mt-8 pt-8 flex flex-col md:flex-row justify-between items-center">
          <div className="flex items-center space-x-2 mb-4 md:mb-0">
            <Clock size={18} className="text-amber-400" />
            <span>Mon-Fri: 7am-8pm, Sat-Sun: 8am-9pm</span>
          </div>
          <p className="text-stone-500 text-sm">
            &copy; {new Date().getFullYear()} Coffee Haven. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  )
}

export default Footer