import ContentArea from "@/components/ContentArea";
import DefaultHeader from "@/components/Header";
import DefaultFooter from "@/components/Footer";
import MenuContent from "@/components/MenuContent";
import FastMenuButtonContainer from "@/components/FastMenuButtonContainer";
import FastMenuButton from "@/components/FastMenuButton";
import {identifier} from "@/lib/commons";

async function getSSRProps() {
    const props = {}
    await Promise.all([
        getHeader().then(comp => props.Header = comp),
        getMainContent().then(comp => props.MenuContent = comp),
        getFooter().then(comp => props.Footer = comp)
    ])
    return props
}

// Honestly fuck javascript for not having enums
const mainMenu = 'main_menu',
    userMenu = 'user_menu',
    adminMenu = 'admin_menu',
    adminGateway = 'admin_gateway',
    adminEureka = 'admin_eureka',
    adminCustomers = 'admin_customers',
    adminOrders = 'admin_orders',
    adminProducts = 'admin_products'


export default async () => {
    const {Header, MenuContent, Footer} = await getSSRProps();


    return (
        <>
            <ContentArea
                header={<Header />}
                mainContent={<MenuContent content={
                    <FastMenuButtonContainer key={await identifier()} id={await identifier(mainMenu)}
                                             buttons={[
                        <FastMenuButton key={await identifier()} label={"User"} id={await identifier(userMenu)}
                                        outerId={await identifier(mainMenu)} menu={[
                            <FastMenuButton key={await identifier()} label={"User Button"} id={await identifier()}
                                            outerId={await identifier('btn1')} menu={[]}/>,
                            <FastMenuButton key={await identifier()} label={"User Button"} id={await identifier()}
                                            outerId={await identifier('btn1')} menu={[]}/>,
                            <FastMenuButton key={await identifier()} label={"User Button"} id={await identifier()}
                                            outerId={await identifier('btn1')} menu={[]}/>,
                            <FastMenuButton key={await identifier()} label={"User Button"} id={await identifier()}
                                            outerId={await identifier('btn1')} menu={[]}/>,
                            <FastMenuButton key={await identifier()} label={"User Button"} id={await identifier()}
                                            outerId={await identifier('btn1')} menu={[]}/>
                        ]} />,
                        <FastMenuButton key={await identifier()} label={"Admin"} id={await identifier(adminMenu)}
                                        outerId={await identifier(mainMenu)} menu={[
                            <FastMenuButton key={await identifier()} label={"Eureka"} id={await identifier(adminEureka)}
                                            outerId={await identifier(adminMenu)} menu={[]}/>,
                            <FastMenuButton key={await identifier()} label={"Gateway"} id={await identifier(adminGateway)}
                                            outerId={await identifier(adminMenu)} menu={[]}/>,
                            <FastMenuButton key={await identifier()} label={"Products"} id={await identifier(adminProducts)}
                                            outerId={await identifier(adminMenu)} menu={[]}/>,
                            <FastMenuButton key={await identifier()} label={"Customers"} id={await identifier(adminCustomers)}
                                            outerId={await identifier(adminMenu)} menu={[]}/>,
                            <FastMenuButton key={await identifier()} label={"Orders"} id={await identifier(adminOrders)}
                                            outerId={await identifier(adminMenu)} menu={[]}/>
                        ]} />
                    ]}/>
                }/>}
                footer={<Footer />}
            />
        </>
    )
}

async function getHeader(opt = "default") {
    return {
        default: DefaultHeader
    }[opt]
}

async function getFooter(opt = "default") {
    return {
        default: DefaultFooter
    }[opt]
}

async function getMainContent(opt = "default") {
    return {
        default: MenuContent
    }[opt]
}