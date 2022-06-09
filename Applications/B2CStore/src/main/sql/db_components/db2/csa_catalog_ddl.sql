


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_catalog_ddl.xml#1 $$Change: 1385662 $

create table csa_clothing_sku (
	sku_id	varchar(40)	not null,
	sku_size	varchar(254)	default null,
	color	varchar(254)	default null
,constraint csa_clothing_p primary key (sku_id)
,constraint csa_clothing_f foreign key (sku_id) references dcs_sku (sku_id));


create table csa_furniture_sku (
	sku_id	varchar(40)	not null,
	wood_finish	varchar(254)	default null
,constraint csa_furniture_p primary key (sku_id)
,constraint csa_furniture_f foreign key (sku_id) references dcs_sku (sku_id));


create table csa_category (
	category_id	varchar(40)	not null,
	hero_image_id	varchar(40)	default null
,constraint csa_category_p primary key (category_id)
,constraint csa_category_fctg foreign key (category_id) references dcs_category (category_id)
,constraint csa_category_fmed foreign key (hero_image_id) references dcs_media (media_id));

create index csa_category3_x on csa_category (hero_image_id);

create table csa_product (
	product_id	varchar(40)	not null,
	brief_description	varchar(254)	default null
,constraint csa_product_p primary key (product_id)
,constraint csa_product_fpro foreign key (product_id) references dcs_product (product_id));


create table csa_catalog (
	catalog_id	varchar(40)	not null,
	root_nav_cat	varchar(40)	not null
,constraint csa_catalog_p primary key (catalog_id)
,constraint csa_catalog_f foreign key (root_nav_cat) references dcs_category (category_id));

create index csa_ctlrtnavcat1_x on csa_catalog (root_nav_cat);

create table csa_sku_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	type	integer	default null,
	description	varchar(254)	default null
,constraint csa_sku_xlate_p primary key (translation_id));


create table csa_clothing_xlate (
	translation_id	varchar(40)	not null,
	sku_size	varchar(254)	default null,
	color	varchar(254)	default null
,constraint csa_clthng_xlate_p primary key (translation_id)
,constraint csa_clthng_xlate_f foreign key (translation_id) references csa_sku_xlate (translation_id));


create table csa_furni_xlate (
	translation_id	varchar(40)	not null,
	wood_finish	varchar(254)	default null
,constraint csa_furni_xlate_p primary key (translation_id)
,constraint csa_furni_xlate_f foreign key (translation_id) references csa_sku_xlate (translation_id));


create table csa_sku_sku_xlate (
	sku_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_sku_sku_xlt_p primary key (sku_id,locale)
,constraint csa_sku_xlate_f foreign key (translation_id) references csa_sku_xlate (translation_id));

create index csa_sku_xlt_tr_id on csa_sku_sku_xlate (translation_id);

create table csa_prd_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	long_description	varchar(4000)	default null,
	brief_description	varchar(254)	default null
,constraint csa_prd_xlate_p primary key (translation_id));


create table csa_prd_prd_xlate (
	product_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_prd_prd_xlt_p primary key (product_id,locale)
,constraint csa_prd_xlate_f foreign key (translation_id) references csa_prd_xlate (translation_id));

create index csa_prd_xlt_tr_id on csa_prd_prd_xlate (translation_id);

create table csa_prd_xlate_kwr (
	translation_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint csa_prd_xlt_kwr_p primary key (translation_id,sequence_num)
,constraint csa_prd_xlt_kwr_f foreign key (translation_id) references csa_prd_xlate (translation_id));

create index csa_prd_xlt_kwr_tr on csa_prd_xlate_kwr (translation_id);

create table csa_cat_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	long_description	varchar(4000)	default null
,constraint csa_cat_xlate_p primary key (translation_id));


create table csa_cat_cat_xlate (
	category_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_cat_cat_xlt_p primary key (category_id,locale)
,constraint csa_cat_xlate_f foreign key (translation_id) references csa_cat_xlate (translation_id));

create index csa_cat_xlt_tr_id on csa_cat_cat_xlate (translation_id);

create table csa_cat_xlate_kwr (
	translation_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint csa_cat_tr_kwr_p primary key (translation_id,sequence_num)
,constraint csa_cat_tr_kwr_f foreign key (translation_id) references csa_cat_xlate (translation_id));

create index csa_cat_xlt_kwr_tr on csa_cat_xlate_kwr (translation_id);
commit;


