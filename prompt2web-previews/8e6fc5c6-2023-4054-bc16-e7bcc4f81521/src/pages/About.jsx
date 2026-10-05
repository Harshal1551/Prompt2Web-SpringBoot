import { Link } from 'react-router-dom'
import { Coffee, Heart, Award, MapPin, Clock, Users, ArrowRight } from 'lucide-react'

const About = () => {
  return (
    <>
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-stone-900 via-stone-800 to-stone-900 text-white overflow-hidden">
        <div className="absolute inset-0 bg-black opacity-50"></div>
        <div
          className="absolute inset-0 bg-cover bg-center"
          style={{
            backgroundImage: 'url("https://images.unsplash.com/photo-1554118811-1e0d58224f36?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80")'
          }}
        ></div>
        <div className="relative section-padding py-20 lg:py-32">
          <div className="container-custom text-center">
            <h1 className="text-5xl lg:text-7xl font-serif font-bold mb-6">Our Story</h1>
            <p className="text-xl lg:text-2xl text-stone-300 max-w-3xl mx-auto">
              From humble beginnings to a community cornerstone, discover the passion behind Coffee Haven.
            </p>
          </div>
        </div>
      </section>

      {/* Story Section */}
      <section className="section-padding bg-amber-50">
        <div className="container-custom">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-4xl font-serif font-bold text-stone-800 mb-6">Born from a Passion for Coffee</h2>
              <p className="text-stone-600 text-lg mb-6">
                In 2010, our founder Sarah Mitchell traveled the world in search of the perfect coffee experience. After visiting countless cafes and meeting passionate roasters from Ethiopia to Colombia, she knew she had to share this passion with her hometown.
              </p>
              <p className="text-stone-600 text-lg mb-6">
                Coffee Haven opened its doors with a simple mission: to serve exceptional coffee in a warm, welcoming environment. We source only the highest quality beans, roast them in small batches, and train our baristas to craft the perfect cup every time.
              </p>
              <p className="text-stone-600 text-lg">
                Today, we're proud to be a cornerstone of the community, hosting local artists, musicians, and events that bring people together through the universal love of coffee.
              </p>
              <div className="mt-8">
                <Link to="/contact" className="btn-primary inline-flex items-center">
                  Visit Us
                  <ArrowRight size={20} className="ml-2" />
                </Link>
              </div>
            </div>
            <div className="relative">
              <div className="aspect-square rounded-2xl overflow-hidden shadow-2xl">
                <div
                  className="w-full h-full bg-cover bg-center"
                  style={{
                    backgroundImage: 'url("https://images.unsplash.com/photo-1511537190424-bbbab87ac5eb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80")'
                  }}
                ></div>
              </div>
              <div className="absolute -bottom-6 -right-6 bg-amber-500 text-white p-6 rounded-xl shadow-lg hidden lg:block">
                <div className="text-center">
                  <div className="text-4xl font-bold">15+</div>
                  <div className="text-amber-100">Years of Passion</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Values Section */}
      <section className="section-padding bg-stone-100">
        <div className="container-custom">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-serif font-bold text-stone-800 mb-4">Our Values</h2>
            <p className="text-stone-600 text-lg max-w-2xl mx-auto">
              These principles guide everything we do, from bean selection to customer service.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center bg-white p-8 rounded-xl shadow-lg">
              <div className="bg-amber-100 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6">
                <Coffee size={40} className="text-amber-600" />
              </div>
              <h3 className="text-2xl font-serif font-bold text-stone-800 mb-3">Quality First</h3>
              <p className="text-stone-600">
                We never compromise on quality. Every bean is carefully selected and roasted to perfection.
              </p>
            </div>

            <div className="text-center bg-white p-8 rounded-xl shadow-lg">
              <div className="bg-amber-100 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6">
                <Heart size={40} className="text-amber-600" />
              </div>
              <h3 className="text-2xl font-serif font-bold text-stone-800 mb-3">Community Focus</h3>
              <p className="text-stone-600">
                We're more than a coffee shop - we're a gathering place that supports local artists and events.
              </p>
            </div>

            <div className="text-center bg-white p-8 rounded-xl shadow-lg">
              <div className="bg-amber-100 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6">
                <Award size={40} className="text-amber-600" />
              </div>
              <h3 className="text-2xl font-serif font-bold text-stone-800 mb-3">Sustainability</h3>
              <p className="text-stone-600">
                We partner with ethical farms and use eco-friendly practices to reduce our environmental impact.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Team Section */}
      <section className="section-padding bg-stone-900 text-white">
        <div className="container-custom">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-serif font-bold mb-4">Meet Our Team</h2>
            <p className="text-stone-300 text-lg max-w-2xl mx-auto">
              The passionate people behind your perfect cup.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center bg-stone-800 p-8 rounded-xl">
              <div className="w-32 h-32 bg-amber-400 rounded-full mx-auto mb-6 flex items-center justify-center text-stone-900 text-4xl font-bold">
                SM
              </div>
              <h3 className="text-2xl font-serif font-bold mb-2">Sarah Mitchell</h3>
              <p className="text-amber-400 mb-4">Founder & Head Roaster</p>
              <p className="text-stone-400">With over 15 years of coffee experience, Sarah oversees every roast to ensure perfection.</p>
            </div>

            <div className="text-center bg-stone-800 p-8 rounded-xl">
              <div className="w-32 h-32 bg-amber-400 rounded-full mx-auto mb-6 flex items-center justify-center text-stone-900 text-4xl font-bold">
                JT
              </div>
              <h3 className="text-2xl font-serif font-bold mb-2">James Torres</h3>
              <p className="text-amber-400 mb-4">Head Barista</p>
              <p className="text-stone-400">James is a latte art champion who trains our baristas and crafts your perfect drink.</p>
            </div>

            <div className="text-center bg-stone-800 p-8 rounded-xl">
              <div className="w-32 h-32 bg-amber-400 rounded-full mx-auto mb-6 flex items-center justify-center text-stone-900 text-4xl font-bold">
                AK
              </div>
              <h3 className="text-2xl font-serif font-bold mb-2">Anna Kim</h3>
              <p className="text-amber-400 mb-4">Pastry Chef</p>
              <p className="text-stone-400">Anna creates our delicious pastries using local ingredients and traditional techniques.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="section-padding bg-amber-500 text-white">
        <div className="container-custom">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
            <div>
              <div className="text-5xl font-bold mb-2">15+</div>
              <div className="text-amber-100">Years in Business</div>
            </div>
            <div>
              <div className="text-5xl font-bold mb-2">50k+</div>
              <div className="text-amber-100">Cups Served Monthly</div>
            </div>
            <div>
              <div className="text-5xl font-bold mb-2">12</div>
              <div className="text-amber-100">Origin Countries</div>
            </div>
            <div>
              <div className="text-5xl font-bold mb-2">4.9</div>
              <div className="text-amber-100">Average Rating</div>
            </div>
          </div>
        </div>
      </section>
    </>
  )
}

export default About