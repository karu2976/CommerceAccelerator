


--      @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_content_mgmt_ddl.xml#1 $$Change: 1385662 $  

create table csa_media_content (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	link_url	varchar(255)	default null,
	alt_text	varchar(255)	default null,
	image_srcset	varchar(1056)	default null,
	image_text_1	varchar(255)	default null,
	image_text_2	varchar(255)	default null,
	image_text_3	varchar(255)	default null,
	image_text_4	varchar(255)	default null
,constraint csa_media_content_p primary key (id,asset_version));


create table csa_media_content_xlate (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	translation_id	varchar(40)	not null,
	name	varchar(255)	default null,
	title	varchar(255)	default null,
	description	varchar(255)	default null,
	link_url	varchar(255)	default null,
	alt_text	varchar(255)	default null,
	image_text_1	varchar(255)	default null,
	image_text_2	varchar(255)	default null,
	image_text_3	varchar(255)	default null,
	image_text_4	varchar(255)	default null
,constraint csa_media_content_xlate_p primary key (translation_id,asset_version));

create index csa_media_cont_wsx on csa_media_content_xlate (workspace_id);
create index csa_media_cont_cix on csa_media_content_xlate (checkin_date);

create table csa_media_media_xlate (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_media_media_xlate_p primary key (id,locale,asset_version));

commit;


