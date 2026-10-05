import { useState } from 'react'
import { MapPin, Phone, Mail, Clock, Send, CheckCircle } from 'lucide-react'

const Contact = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    message: ''
  })
  const [isSubmitted, setIsSubmitted] = useState(false)

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    // In a real app, you would send this data to a server
    console.log('Form submitted:', formData)
    setIsSubmitted(true)
    setFormData({ name: '', email: '', message: '' })
    setTimeout(() => setIsSubmitted(false), 5000)
  }

  return (
    <div className="section-padding bg-amber-50">
      <div className="container-custom">
        <div className="text-center mb-12">
          <h1 className="text-4xl lg:text-5xl font-serif font-bold text-stone-800 mb-4">Get In Touch</h1>
          <p className="text-stone-600 text-lg max-w-2xl mx-auto">
            We'd love to hear from you. Visit us, call us, or send us a message.
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          {/* Contact Information */}
          <div>
            <h2 className="text-3xl font-serif font-bold text-stone-800 mb-6">Visit Our Cafe</h2>
            <p className="text-stone-600 text-lg mb-8">
              Come experience the Coffee Haven difference for yourself. We're located in the heart of downtown, easy to find and visit.
            </p>

            <div className="space-y-6">
              <div className="flex items-start space-x-4">
                <div className="bg-amber-100 p-3 rounded-lg flex-shrink-0">
                  <MapPin size={24} className="text-amber-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-stone-800 text-lg">Address</h3>
                  <p className="text-stone-600">123 Coffee Street, Brew City, BC 12345</p>
                </div>
              </div>

              <div className="flex items-start space-x-4">
                <div className="bg-amber-100 p-3 rounded-lg flex-shrink-0">
                  <Phone size={24} className="text-amber-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-stone-800 text-lg">Phone</h3>
                  <p className="text-stone-600">(555) 123-4567</p>
                </div>
              </div>

              <div className="flex items-start space-x-4">
                <div className="bg-amber-100 p-3 rounded-lg flex-shrink-0">
                  <Mail size={24} className="text-amber-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-stone-800 text-lg">Email</h3>
                  <p className="text-stone-600">hello@coffehaven.com</p>
                </div>
              </div>

              <div className="flex items-start space-x-4">
                <div className="bg-amber-100 p-3 rounded-lg flex-shrink-0">
                  <Clock size={24} className="text-amber-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-stone-800 text-lg">Hours</h3>
                  <p className="text-stone-600">Mon-Fri: 7am-8pm</p>
                  <p className="text-stone-600">Sat-Sun: 8am-9pm</p>
                </div>
              </div>
            </div>
          </div>

          {/* Contact Form */}
          <div className="bg-white p-8 rounded-2xl shadow-xl">
            <h2 className="text-3xl font-serif font-bold text-stone-800 mb-6">Send Us a Message</h2>
            {isSubmitted ? (
              <div className="text-center py-8">
                <CheckCircle size={64} className="mx-auto text-green-500 mb-4" />
                <h3 className="text-2xl font-serif font-bold text-stone-800 mb-2">Message Sent!</h3>
                <p className="text-stone-600">Thank you for reaching out. We'll get back to you soon.</p>
              </div>
            ) : (
              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label htmlFor="name" className="block text-stone-700 font-medium mb-2">
                    Your Name
                  </label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 rounded-lg border border-stone-300 focus:outline-none focus:ring-2 focus:ring-amber-500"
                    placeholder="Enter your name"
                  />
                </div>

                <div>
                  <label htmlFor="email" className="block text-stone-700 font-medium mb-2">
                    Email Address
                  </label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 rounded-lg border border-stone-300 focus:outline-none focus:ring-2 focus:ring-amber-500"
                    placeholder="Enter your email"
                  />
                </div>

                <div>
                  <label htmlFor="message" className="block text-stone-700 font-medium mb-2">
                    Message
                  </label>
                  <textarea
                    id="message"
                    name="message"
                    value={formData.message}
                    onChange={handleChange}
                    rows={5}
                    required
                    className="w-full px-4 py-3 rounded-lg border border-stone-300 focus:outline-none focus:ring-2 focus:ring-amber-500"
                    placeholder="How can we help you?"
                  ></textarea>
                </div>

                <button
                  type="submit"
                  className="w-full btn-primary py-3 px-6 rounded-lg font-semibold flex items-center justify-center"
                >
                  <Send size={20} className="mr-2" />
                  Send Message
                </button>
              </form>
            )}
          </div>
        </div>

        {/* Map Section */}
        <div className="mt-16">
          <div className="bg-stone-300 rounded-2xl overflow-hidden shadow-xl" style={{ height: '400px' }}>
            <div className="w-full h-full flex items-center justify-center text-stone-600">
              <div className="text-center">
                <MapPin size={64} className="mx-auto mb-4" />
                <p className="text-lg font-semibold">Interactive Map Would Appear Here</p>
                <p className="text-sm">123 Coffee Street, Brew City</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Contact