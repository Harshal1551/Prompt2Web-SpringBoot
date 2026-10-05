const About = () => {
  return (
    <section id='about' className='py-16'>
      <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8'>
        <h2 className='text-3xl font-bold text-center mb-8'>About FitGym</h2>
        <div className='grid grid-cols-1 gap-8 md:grid-cols-2 items-center'>
          <div>
            <p className='mb-4'>We are dedicated to helping you achieve your fitness goals through personalized training, modern equipment, and a supportive community.</p>
            <p className='mb-4'>Our certified trainers bring years of experience and passion to every session, ensuring you get the most effective and safe workouts.</p>
            <p>Join us today and transform your life!</p>
          </div>
          <div>
            <img src='https://images.unsplash.com/photo-1526485284401-64b804cf2f35?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwzNjUyOXwwfDF8c2VhcmNofDF8fGdltYWl8ZW58MHx8fHwxNjc5MDQyMzU0' alt='Gym interior' className='rounded-lg shadow-md' />
          </div>
        </div>
      </div>
    </section>
  );
};

export default About;