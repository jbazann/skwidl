import styles from "./menu-content.module.css";

export default function ({content}) {
    return (
        <div className={`relative-container flex-col center-children ${""}`}>
            <div className={`flex-col center-children auto-scroll-y ${styles.menuContainer}`}>
                {content}
            </div>
        </div>
    );
};