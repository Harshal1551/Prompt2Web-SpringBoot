const Contact = () => {
  return (
    <section id='contact' className='py-16 bg-gray-50'>
      <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8'>
        <h2 className='text-3xl font-bold text-center mb-8'>Contact Us</h2>
        <div className='grid gap-8 md:grid-cols-2'>
          <form className='space-y-6'>
            <div>
              <label htmlFor='name' className='block text-sm font-medium text-gray-700 mb-2'>Name</label>
              <input type='text' id='name' name='name' required className='w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500' />
            </div>
            <div>
              <label htmlFor='email' className='block text-sm font-medium text-gray-700 mb-2'>Email</label>
              <input type='email' id='email' name='email' required className='w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500' />
            </div>
            <div>
              <label htmlFor='message' className='block text-sm font-medium text-gray-700 mb-2'>Message</label>
              <textarea id='message' name='message' rows='4' required className='w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500' />
            </div>
            <button type='submit' className='w-full bg-indigo-600 text-white px-6 py-3 rounded-md hover:bg-indigo-700 transition'>Send Message</button>
          </form>
          <div className='space-y-4'>
            <p className='text-gray-600'>
              <span className='font-medium'>Address:</span> 123 Fitness Street, Health City
            </p>
            <p className='text-gray-600'>
              <span className='font-medium'>Phone:</span> (555) 123-4567
            </p>
            <p className='text-gray-600'>
              <span className='font-medium'>Email:</span> info@fitgym.com
            </p>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Contact;