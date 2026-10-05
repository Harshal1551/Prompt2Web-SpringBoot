import { useState } from 'react';
import { Menu, X } from 'lucide-react';

const Header = () => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <header className='bg-white shadow-md sticky top-0 z-50'>
      <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8'>
        <div className='flex justify-between h-16'>
          <div className='flex items-center'>
            <span className='text-xl font-semibold text-indigo-600'>FitGym</span>
          </div>
          <div className='hidden md:flex items-center space-x-6'>
            <nav>
              <a href='#hero' className='hover:text-indigo-600 transition'>Home</a>
              <a href='#about' className='hover:text-indigo-600 transition'>About</a>
              <a href='#services' className='hover:text-indigo-600 transition'>Services</a>
              <a href='#classes' className='hover:text-indigo-600 transition'>Classes</a>
              <a href='#contact' className='hover:text-indigo-600 transition'>Contact</a>
            </nav>
          </div>
          <div className='md:hidden'>
            <button
              onClick={() => setIsOpen(!isOpen)}
              className='text-indigo-600 hover:text-indigo-800'
              aria-label='Toggle menu'
            >
              {isOpen ? <X className='h-6 w-6' /> : <Menu className='h-6 w-6' />}
            </button>
          </div>
        </div>
        {isOpen && (
          <div className='md:hidden px-2 pt-2 pb-3 space-y-1'>
            <nav>
              <a href='#hero' className='block px-3 py-2 rounded-md text-base font-medium hover:bg-indigo-50 hover:text-indigo-600'>Home</a>
              <a href='#about' className='block px-3 py-2 rounded-md text-base font-medium hover:bg-indigo-50 hover:text-indigo-600'>About</a>
              <a href='#services' className='block px-3 py-2 rounded-md text-base font-medium hover:bg-indigo-50 hover:text-indigo-600'>Services</a>
              <a href='#classes' className='block px-3 py-2 rounded-md text-base font-medium hover:bg-indigo-50 hover:text-indigo-600'>Classes</a>
              <a href='#contact' className='block px-3 py-2 rounded-md text-base font-medium hover:bg-indigo-50 hover:text-indigo-600'>Contact</a>
            </nav>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;