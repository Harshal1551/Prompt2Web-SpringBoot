import Header from './components/Header';
import Hero from './components/Hero';
import About from './components/About';
import Services from './components/Services';
import Classes from './components/Classes';
import Contact from './components/Contact';
import Footer from './components/Footer';

function App() {
  return (
    <div className='min-h-screen bg-gray-50 text-gray-900'>
      <Header />
      <main>
        <Hero />
        <About />
        <Services />
        <Classes />
        <Contact />
      </main>
      <Footer />
    </div>
  );
}

export default App;