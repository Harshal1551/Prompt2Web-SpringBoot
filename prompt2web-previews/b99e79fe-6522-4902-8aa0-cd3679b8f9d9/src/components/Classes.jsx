const Classes = () => {
  return (
    <section id='classes' className='py-16'>
      <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8'>
        <h2 className='text-3xl font-bold text-center mb-8'>Class Schedule</h2>
        <div className='space-y-6'>
          <div className='bg-white p-6 rounded-lg shadow flex items-center space-x-4'>
            <div className='w-12 h-12 bg-indigo-100 rounded-full flex items-center justify-center'>
              <span className='text-indigo-600 text-2xl'>💪</span>
            </div>
            <div>
              <h3 className='text-lg font-semibold'>Morning Bootcamp</h3>
              <p className='text-gray-500'>Mon, Wed, Fri • 6:00 AM - 7:00 AM</p>
            </div>
          </div>
          <div className='bg-white p-6 rounded-lg shadow flex items-center space-x-4'>
            <div className='w-12 h-12 bg-indigo-100 rounded-full flex items-center justify-center'>
              <span className='text-indigo-600 text-2xl'>🧘</span>
            </div>
            <div>
              <h3 className='text-lg font-semibold'>Yoga Flow</h3>
              <p className='text-gray-500'>Tue, Thu • 7:00 PM - 8:00 PM</p>
            </div>
          </div>
          <div className='bg-white p-6 rounded-lg shadow flex items-center space-x-4'>
            <div className='w-12 h-12 bg-indigo-100 rounded-full flex items-center justify-center'>
              <span className='text-indigo-600 text-2xl'>🚴</span>
            </div>
            <div>
              <h3 className='text-lg font-semibold'>Spin Class</h3>
              <p className='text-gray-500'>Mon, Wed, Fri • 5:30 PM - 6:30 PM</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Classes;