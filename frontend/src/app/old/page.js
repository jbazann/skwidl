import Image from "next/image";
import styles from "../page.module.css";
import Link from "next/link";

export default function Home() {
  return (
    <main className={styles.main}>
      <div>
          <h1>The SKWIDL Project</h1>
          <Link href="/clientes">
            <button className={styles.botones}>Customers</button>
          </Link>
          <Link href="/products">
            <button className={styles.botones} >Products</button>
          </Link>
          <Link href="/pedidos">
            <button className={styles.botones}>Orders</button>
          </Link>
      </div>
    </main>
  );
}
