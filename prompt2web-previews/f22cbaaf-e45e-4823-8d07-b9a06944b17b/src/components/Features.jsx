import React from 'react'

const features = [
  {
    name: 'Lightning Fast Performance',
    description:
      'Our solutions are optimized for speed and reliability, ensuring your business runs at peak efficiency.',
    icon: '⚡',
    color: 'yellow'
  },
  {
    name: 'Advanced Security',
    description:
      'Enterprise-grade security protocols protect your data and give you peace of mind.',
    icon: '🔒',
    color: 'green'
  },
  {
    name: 'AI-Powered Analytics',
    description:
      'Leverage machine learning to gain insights and make data-driven decisions.',
    icon: '📊',
    color: 'blue'
  },
  {
    name: '24/7 Support',
    description:
      'Round-the-clock dedicated support ensures you never face challenges alone.',
    icon: 'Customer Service',
    color: 'purple'
  },
  {
    name: 'Seamless Integration',
    description:
      'Easily connect with your existing tools and platforms for unified workflows.',
    icon: '🔗',
    color: 'indigo'
  },
  {
    name: 'Scalable Solutions',
    description:
      'Grow your business without limitations with our flexible and scalable architecture.',
    icon: '📈',
    color: 'pink'
  }
]

const Features = () => {
  return (
    <section id="features" className="py-20 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

        <div className="text-center mb-16">
          <h2 className="text-3xl lg:text-4xl font-bold text-gray-900 mb-4">
            Why Choose Our{' '}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">
              Solutions?
            </span>
          </h2>

          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            We deliver comprehensive business solutions designed to address
            your unique challenges and drive success.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">

          {features.map((feature, index) => (
            <div
              key={index}
              className="bg-white rounded-xl p-8 shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-2 border border-gray-100"
            >

              <div
                className={`w-14 h-14 bg-${feature.color}-100 rounded-lg flex items-center justify-center mb-6 text-2xl`}
              >
                {feature.icon}
              </div>

              <h3 className="text-xl font-bold text-gray-900 mb-4">
                {feature.name}
              </h3>

              <p className="text-gray-600 leading-relaxed">
                {feature.description}
              </p>

            </div>
          ))}

        </div>

      </div>
    </section>
  )
}

export default Features