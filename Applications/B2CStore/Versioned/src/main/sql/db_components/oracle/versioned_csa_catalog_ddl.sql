


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_catalog_ddl.xml#1 $$Change: 1385662 $

      alter session set NLS_LENGTH_SEMANTICS='CHAR';
    

create table csa_clothing_sku (
	asset_version	number(19)	not null,
	sku_id	varchar2(40)	not null,
	sku_size	varchar2(254)	null,
	color	varchar2(254)	null
,constraint csa_clothing_p primary key (sku_id,asset_version));


create table csa_furniture_sku (
	asset_version	number(19)	not null,
	sku_id	varchar2(40)	not null,
	wood_finish	varchar2(254)	null
,constraint csa_furniture_p primary key (sku_id,asset_version));


create table csa_category (
	asset_version	number(19)	not null,
	category_id	varchar2(40)	not null,
	hero_image_id	varchar2(40)	null
,constraint csa_category_p primary key (category_id,asset_version));


create table csa_product (
	asset_version	number(19)	not null,
	product_id	varchar2(40)	not null,
	brief_description	varchar2(254)	null
,constraint csa_product_p primary key (product_id,asset_version));


create table csa_catalog (
	asset_version	number(19)	not null,
	catalog_id	varchar2(40)	not null,
	root_nav_cat	varchar2(40)	not null
,constraint csa_catalog_p primary key (catalog_id,asset_version));


create table csa_sku_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	translation_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	type	number(10)	null,
	description	varchar2(254)	null
,constraint csa_sku_xlate_p primary key (translation_id,asset_version));

create index csa_sku_xlate_wsx on csa_sku_xlate (workspace_id);
create index csa_sku_xlate_cix on csa_sku_xlate (checkin_date);

create table csa_clothing_xlate (
	asset_version	number(19)	not null,
	translation_id	varchar2(40)	not null,
	sku_size	varchar2(254)	null,
	color	varchar2(254)	null
,constraint csa_clthng_xlate_p primary key (translation_id,asset_version));


create table csa_furni_xlate (
	asset_version	number(19)	not null,
	translation_id	varchar2(40)	not null,
	wood_finish	varchar2(254)	null
,constraint csa_furni_xlate_p primary key (translation_id,asset_version));


create table csa_sku_sku_xlate (
	asset_version	number(19)	not null,
	sku_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint csa_sku_sku_xlt_p primary key (sku_id,locale,asset_version));

create index csa_sku_xlt_tr_id on csa_sku_sku_xlate (translation_id);

create table csa_prd_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	translation_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	long_description	varchar2(4000)	null,
	brief_description	varchar2(254)	null
,constraint csa_prd_xlate_p primary key (translation_id,asset_version));

create index csa_prd_xlate_wsx on csa_prd_xlate (workspace_id);
create index csa_prd_xlate_cix on csa_prd_xlate (checkin_date);

create table csa_prd_prd_xlate (
	asset_version	number(19)	not null,
	product_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint csa_prd_prd_xlt_p primary key (product_id,locale,asset_version));

create index csa_prd_xlt_tr_id on csa_prd_prd_xlate (translation_id);

create table csa_prd_xlate_kwr (
	asset_version	number(19)	not null,
	translation_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar2(254)	not null
,constraint csa_prd_xlt_kwr_p primary key (translation_id,sequence_num,asset_version));

create index csa_prd_xlt_kwr_tr on csa_prd_xlate_kwr (translation_id);

create table csa_cat_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	translation_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	long_description	varchar2(4000)	null
,constraint csa_cat_xlate_p primary key (translation_id,asset_version));

create index csa_cat_xlate_wsx on csa_cat_xlate (workspace_id);
create index csa_cat_xlate_cix on csa_cat_xlate (checkin_date);

create table csa_cat_cat_xlate (
	asset_version	number(19)	not null,
	category_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint csa_cat_cat_xlt_p primary key (category_id,locale,asset_version));

create index csa_cat_xlt_tr_id on csa_cat_cat_xlate (translation_id);

create table csa_cat_xlate_kwr (
	asset_version	number(19)	not null,
	translation_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar2(254)	not null
,constraint csa_cat_tr_kwr_p primary key (translation_id,sequence_num,asset_version));

create index csa_cat_xlt_kwr_tr on csa_cat_xlate_kwr (translation_id);

      alter session set NLS_LENGTH_SEMANTICS='BYTE';
    



