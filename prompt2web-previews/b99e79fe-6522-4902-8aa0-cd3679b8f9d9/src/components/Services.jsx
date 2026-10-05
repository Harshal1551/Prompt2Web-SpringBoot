const Services = () => {
  return (
    <section id='services' className='py-16 bg-gray-50'>
      <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8'>
        <h2 className='text-3xl font-bold text-center mb-8'>Our Services</h2>
        <div className='grid gap-6 sm:grid-cols-2 lg:grid-cols-3'>
          <div className='bg-white p-6 rounded-lg shadow'>
            <h3 className='text-xl font-semibold mb-4'>Personal Training</h3>
            <p className='text-gray-600'>One-on-one sessions tailored to your goals.</p>
          </div>
          <div className='bg-white p-6 rounded-lg shadow'>
            <h3 className='text-xl font-semibold mb-4'>Group Classes</h3>
            <p className='text-gray-600'>Energetic workouts with varied routines.</p>
          </div>
          <div className='bg-white p-6 rounded-lg shadow'>
            <h3 className='text-xl font-semibold mb-4'>Nutrition Guidance</h3>
            <p className='text-gray-600'>Meal planning and diet advice.</p>
          </div>
          <div className='bg-white p-6 rounded-lg shadow'>
            <h3 className='text-xl font-semibold mb-4'>Weightlifting Area</h3>
            <p className='text-gray-600'>Full range of free weights and machines.</p>
          </div>
          <div className='bg-white p-6 rounded-lg shadow'>
            <h3 className='text-xl font-semibold mb-4'>Cardio Zone</h3>
            <p className='text-gray-600'>Treadmills, bikes, and rowers.</p>
          </div>
          <div className='bg-white p-6 rounded-lg shadow'>
            <h3 className='text-xl font-semibold mb-4'>Recovery & Stretching</h3>
            <p className='text-gray-600'>Foam rolling, stretching, and massage.</p>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Services;