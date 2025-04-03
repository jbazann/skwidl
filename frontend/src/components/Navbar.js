"use client";
import Link from 'next/link';

const Navbar = () => {
  return (
    <nav>
      <Link href="/clientes">Customers</Link>
      <Link href="/products">Products</Link>
      <Link href="/pedidos">Orders</Link>
      <style jsx>{`
        nav {
          background: #333;
          padding: 10px;
        }
        a {
          color: white;
          margin-right: 10px;
          text-decoration: none;
        }
      `}</style>
    </nav>
  );
};

export default Navbar;
