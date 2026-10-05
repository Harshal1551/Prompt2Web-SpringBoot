const Hero = () => {
  return (
    <section id='hero' className='relative min-h-[60vh] flex items-center justify-center text-white'>
      <div className='absolute inset-0' style={{ backgroundImage: `url('https://images.unsplash.com/photo-1517836357463-d25dfe93410a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwzNjUyOXwwfDF8c2VhcmNofDF8fGdltYWl8ZW58MHx8fHwxNjc5MDQyMzAw')`, backgroundSize: 'cover', backgroundPosition: 'center' }}></div>
      <div className='relative bg-black/50 p-6 rounded-lg text-center'>
        <h1 className='text-4xl md:text-5xl font-bold mb-4'>Welcome to FitGym</h1>
        <p className='text-lg mb-6'>Achieve your fitness goals with state-of-the-art equipment and expert trainers.</p>
        <a href='#classes' className='bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-md transition'>Join Now</a>
      </div>
    </section>
  );
};

export default Hero;