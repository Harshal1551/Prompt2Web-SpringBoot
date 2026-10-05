import { Link } from 'react-router-dom'
import { Coffee, Star, Award, Heart, ArrowRight } from 'lucide-react'

const Home = () => {
  return (
    <>
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-stone-900 via-stone-800 to-stone-900 text-white overflow-hidden">
        <div className="absolute inset-0 bg-black opacity-50"></div>
        <div
          className="absolute inset-0 bg-cover bg-center"
          style={{
            backgroundImage: 'url("https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80")'
          }}
        ></div>
        <div className="relative section-padding py-20 lg:py-32">
          <div className="container-custom text-center lg:text-left">
            <div className="max-w-3xl">
              <h1 className="text-5xl lg:text-7xl font-serif font-bold mb-6 leading-tight">
                Crafted with <span className="text-amber-400">Passion</span>,<br />
                Served with <span className="text-amber-400">Perfection</span>
              </h1>
              <p className="text-xl lg:text-2xl text-stone-300 mb-8 max-w-2xl">
                Experience the aroma of freshly ground beans and the warmth of our artisanal blends.
              </p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start">
                <Link to="/menu" className="btn-primary inline-flex items-center justify-center">
                  Explore Our Menu
                  <ArrowRight size={20} className="ml-2" />
                </Link>
                <Link to="/about" className="btn-secondary inline-flex items-center justify-center bg-stone-700 text-white hover:bg-stone-600">
                  Our Story
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="section-padding bg-amber-50">
        <div className="container-custom">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-serif font-bold text-stone-800 mb-4">Why Choose Coffee Haven</h2>
            <p className="text-stone-600 text-lg max-w-2xl mx-auto">
              We believe that every cup of coffee should be an extraordinary experience.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center group">
              <div className="bg-amber-100 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6 group-hover:bg-amber-200 transition-colors">
                <Coffee size={40} className="text-amber-600" />
              </div>
              <h3 className="text-2xl font-serif font-bold text-stone-800 mb-3">Premium Beans</h3>
              <p className="text-stone-600">
                Sourced from the finest coffee-growing regions around the world, carefully selected for exceptional flavor.
              </p>
            </div>

            <div className="text-center group">
              <div className="bg-amber-100 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6 group-hover:bg-amber-200 transition-colors">
                <Award size={40} className="text-amber-600" />
              </div>
              <h3 className="text-2xl font-serif font-bold text-stone-800 mb-3">Expert Roasting</h3>
              <p className="text-stone-600">
                Our master roasters bring out the unique character of each bean through small-batch artisanal roasting.
              </p>
            </div>

            <div className="text-center group">
              <div className="bg-amber-100 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6 group-hover:bg-amber-200 transition-colors">
                <Heart size={40} className="text-amber-600" />
              </div>
              <h3 className="text-2xl font-serif font-bold text-stone-800 mb-3">Cozy Atmosphere</h3>
              <p className="text-stone-600">
                Whether you're catching up with friends or working remotely, our shop provides a warm and welcoming environment.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Products */}
      <section className="section-padding bg-stone-100">
        <div className="container-custom">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-serif font-bold text-stone-800 mb-4">Featured Favorites</h2>
            <p className="text-stone-600 text-lg max-w-2xl mx-auto">
              Discover our most beloved coffee creations.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Product 1 */}
            <div className="bg-white rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300">
              <div className="h-64 bg-gradient-to-br from-amber-200 to-amber-300 flex items-center justify-center">
                <Coffee size={80} className="text-amber-700" />
              </div>
              <div className="p-6">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-xl font-serif font-bold text-stone-800">Signature Espresso</h3>
                  <div className="flex items-center text-amber-500">
                    <Star size={16} fill="currentColor" />
                    <span className="ml-1 text-stone-600">4.9</span>
                  </div>
                </div>
                <p className="text-stone-600 mb-4">Rich, bold, and perfectly balanced espresso shot with notes of dark chocolate and caramel.</p>
                <div className="flex items-center justify-between">
                  <span className="text-2xl font-bold text-amber-600">$3.50</span>
                  <button className="btn-primary text-sm py-2 px-4">Add to Cart</button>
                </div>
              </div>
            </div>

            {/* Product 2 */}
            <div className="bg-white rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300">
              <div className="h-64 bg-gradient-to-br from-amber-200 to-amber-300 flex items-center justify-center">
                <Coffee size={80} className="text-amber-700" />
              </div>
              <div className="p-6">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-xl font-serif font-bold text-stone-800">Vanilla Latte</h3>
                  <div className="flex items-center text-amber-500">
                    <Star size={16} fill="currentColor" />
                    <span className="ml-1 text-stone-600">4.8</span>
                  </div>
                </div>
                <p className="text-stone-600 mb-4">Smooth espresso with steamed milk and a hint of natural vanilla syrup.</p>
                <div className="flex items-center justify-between">
                  <span className="text-2xl font-bold text-amber-600">$5.25</span>
                  <button className="btn-primary text-sm py-2 px-4">Add to Cart</button>
                </div>
              </div>
            </div>

            {/* Product 3 */}
            <div className="bg-white rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300">
              <div className="h-64 bg-gradient-to-br from-amber-200 to-amber-300 flex items-center justify-center">
                <Coffee size={80} className="text-amber-700" />
              </div>
              <div className="p-6">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-xl font-serif font-bold text-stone-800">Cold Brew</h3>
                  <div className="flex items-center text-amber-500">
                    <Star size={16} fill="currentColor" />
                    <span className="ml-1 text-stone-600">4.7</span>
                  </div>
                </div>
                <p className="text-stone-600 mb-4">Slow-steeped for 24 hours, resulting in a smooth, naturally sweet coffee concentrate.</p>
                <div className="flex items-center justify-between">
                  <span className="text-2xl font-bold text-amber-600">$4.75</span>
                  <button className="btn-primary text-sm py-2 px-4">Add to Cart</button>
                </div>
              </div>
            </div>
          </div>

          <div className="text-center mt-12">
            <Link to="/menu" className="btn-primary inline-flex items-center">
              View Full Menu
              <ArrowRight size={20} className="ml-2" />
            </Link>
          </div>
        </div>
      </section>

      {/* Testimonials */}
      <section className="section-padding bg-stone-900 text-white">
        <div className="container-custom">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-serif font-bold mb-4">What Our Customers Say</h2>
            <p className="text-stone-300 text-lg max-w-2xl mx-auto">
              Don't just take our word for it - hear from our coffee lovers.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-stone-800 p-8 rounded-xl">
              <div className="flex items-center mb-4">
                {[...Array(5)].map((_, i) => (
                  <Star key={i} size={18} className="text-amber-400" fill="currentColor" />
                ))}
              </div>
              <p className="text-stone-300 mb-6">"The best coffee I've ever had. The atmosphere is perfect for both work and relaxation."</p>
              <div className="flex items-center">
                <div className="w-12 h-12 bg-amber-400 rounded-full flex items-center justify-center text-stone-900 font-bold mr-4">
                  SJ
                </div>
                <div>
                  <h4 className="font-semibold">Sarah Johnson</h4>
                  <p className="text-stone-400 text-sm">Regular Customer</p>
                </div>
              </div>
            </div>

            <div className="bg-stone-800 p-8 rounded-xl">
              <div className="flex items-center mb-4">
                {[...Array(5)].map((_, i) => (
                  <Star key={i} size={18} className="text-amber-400" fill="currentColor" />
                ))}
              </div>
              <p className="text-stone-300 mb-6">"Their cold brew is absolutely divine. I come here every morning before work now."</p>
              <div className="flex items-center">
                <div className="w-12 h-12 bg-amber-400 rounded-full flex items-center justify-center text-stone-900 font-bold mr-4">
                  MT
                </div>
                <div>
                  <h4 className="font-semibold">Mike Thompson</h4>
                  <p className="text-stone-400 text-sm">Coffee Enthusiast</p>
                </div>
              </div>
            </div>

            <div className="bg-stone-800 p-8 rounded-xl">
              <div className="flex items-center mb-4">
                {[...Array(5)].map((_, i) => (
                  <Star key={i} size={18} className="text-amber-400" fill="currentColor" />
                ))}
              </div>
              <p className="text-stone-300 mb-6">"The baristas are so knowledgeable and always make you feel at home. A true gem!"</p>
              <div className="flex items-center">
                <div className="w-12 h-12 bg-amber-400 rounded-full flex items-center justify-center text-stone-900 font-bold mr-4">
                  EL
                </div>
                <div>
                  <h4 className="font-semibold">Emma Lee</h4>
                  <p className="text-stone-400 text-sm">Local Resident</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Newsletter */}
      <section className="section-padding bg-amber-500 text-white">
        <div className="container-custom text-center">
          <h2 className="text-4xl font-serif font-bold mb-4">Join Our Coffee Club</h2>
          <p className="text-amber-100 text-lg mb-8 max-w-2xl mx-auto">
            Subscribe to get exclusive offers, new blend announcements, and a free coffee on your first visit.
          </p>
          <form className="max-w-md mx-auto flex flex-col sm:flex-row gap-4">
            <input
              type="email"
              placeholder="Enter your email address"
              className="flex-1 px-4 py-3 rounded-lg text-stone-800 focus:outline-none focus:ring-2 focus:ring-stone-300"
              required
            />
            <button type="submit" className="bg-stone-900 hover:bg-stone-800 text-white font-semibold py-3 px-6 rounded-lg transition-colors duration-200">
              Subscribe
            </button>
          </form>
        </div>
      </section>
    </>
  )
}

export default Home