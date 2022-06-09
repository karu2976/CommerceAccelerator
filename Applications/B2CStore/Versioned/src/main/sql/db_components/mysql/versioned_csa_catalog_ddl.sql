


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_catalog_ddl.xml#1 $$Change: 1385662 $

create table csa_clothing_sku (
	asset_version	bigint	not null,
	sku_id	varchar(40)	not null,
	sku_size	nvarchar(254)	null,
	color	nvarchar(254)	null
,constraint csa_clothing_p primary key (sku_id,asset_version));


create table csa_furniture_sku (
	asset_version	bigint	not null,
	sku_id	varchar(40)	not null,
	wood_finish	nvarchar(254)	null
,constraint csa_furniture_p primary key (sku_id,asset_version));


create table csa_category (
	asset_version	bigint	not null,
	category_id	varchar(40)	not null,
	hero_image_id	varchar(40)	null
,constraint csa_category_p primary key (category_id,asset_version));


create table csa_product (
	asset_version	bigint	not null,
	product_id	varchar(40)	not null,
	brief_description	nvarchar(254)	null
,constraint csa_product_p primary key (product_id,asset_version));


create table csa_catalog (
	asset_version	bigint	not null,
	catalog_id	varchar(40)	not null,
	root_nav_cat	varchar(40)	not null
,constraint csa_catalog_p primary key (catalog_id,asset_version));


create table csa_sku_xlate (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	type	integer	null,
	description	varchar(254)	null
,constraint csa_sku_xlate_p primary key (translation_id,asset_version));

create index csa_sku_xlate_wsx on csa_sku_xlate (workspace_id);
create index csa_sku_xlate_cix on csa_sku_xlate (checkin_date);

create table csa_clothing_xlate (
	asset_version	bigint	not null,
	translation_id	varchar(40)	not null,
	sku_size	varchar(254)	null,
	color	varchar(254)	null
,constraint csa_clthng_xlate_p primary key (translation_id,asset_version));


create table csa_furni_xlate (
	asset_version	bigint	not null,
	translation_id	varchar(40)	not null,
	wood_finish	varchar(254)	null
,constraint csa_furni_xlate_p primary key (translation_id,asset_version));


create table csa_sku_sku_xlate (
	asset_version	bigint	not null,
	sku_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_sku_sku_xlt_p primary key (sku_id,locale,asset_version));

create index csa_sku_xlt_tr_id on csa_sku_sku_xlate (translation_id);

create table csa_prd_xlate (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	long_description	nvarchar(4000)	null,
	brief_description	varchar(254)	null
,constraint csa_prd_xlate_p primary key (translation_id,asset_version));

create index csa_prd_xlate_wsx on csa_prd_xlate (workspace_id);
create index csa_prd_xlate_cix on csa_prd_xlate (checkin_date);

create table csa_prd_prd_xlate (
	asset_version	bigint	not null,
	product_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_prd_prd_xlt_p primary key (product_id,locale,asset_version));

create index csa_prd_xlt_tr_id on csa_prd_prd_xlate (translation_id);

create table csa_prd_xlate_kwr (
	asset_version	bigint	not null,
	translation_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint csa_prd_xlt_kwr_p primary key (translation_id,sequence_num,asset_version));

create index csa_prd_xlt_kwr_tr on csa_prd_xlate_kwr (translation_id);

create table csa_cat_xlate (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	long_description	nvarchar(4000)	null
,constraint csa_cat_xlate_p primary key (translation_id,asset_version));

create index csa_cat_xlate_wsx on csa_cat_xlate (workspace_id);
create index csa_cat_xlate_cix on csa_cat_xlate (checkin_date);

create table csa_cat_cat_xlate (
	asset_version	bigint	not null,
	category_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_cat_cat_xlt_p primary key (category_id,locale,asset_version));

create index csa_cat_xlt_tr_id on csa_cat_cat_xlate (translation_id);

create table csa_cat_xlate_kwr (
	asset_version	bigint	not null,
	translation_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint csa_cat_tr_kwr_p primary key (translation_id,sequence_num,asset_version));

create index csa_cat_xlt_kwr_tr on csa_cat_xlate_kwr (translation_id);
commit;


