


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_order_ddl.xml#1 $$Change: 1385662 $

      alter table dcspp_credit_card alter column credit_card_number set data type varchar(80);
    

create table csa_item_price (
	item_price_info_id	varchar(40)	not null,
	tax_price_info_id	varchar(40)	default null
,constraint csa_item_price_p primary key (item_price_info_id)
,constraint csa_item_price_f foreign key (item_price_info_id) references dcspp_amount_info (amount_info_id));


create table csa_tax_price (
	amount_info_id	varchar(40)	not null,
	district_tax	numeric(19,7)	default null
,constraint csa_tax_price_p primary key (amount_info_id)
,constraint csa_tax_price_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));


create table csa_ship_price (
	ship_price_info_id	varchar(40)	not null,
	tax_price_info_id	varchar(40)	default null
,constraint csa_ship_price_p primary key (ship_price_info_id)
,constraint csa_ship_price_f foreign key (ship_price_info_id) references dcspp_amount_info (amount_info_id));

commit;


